package com.zouth.lab04;

import com.zouth.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCountMapper {
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    private String inTopic, outTopic;

    public WordCountMapper(String inTopic, String outTopic, Optional<String> groupId) {
        this.inTopic = inTopic;
        this.outTopic = outTopic;

        this.consumer = KafkaUtil.createConsumer(groupId.orElse("word-count-mapper"));
        this.producer = KafkaUtil.createProducer();
    }

    public void run() {
        this.consumer.subscribe(List.of(inTopic));

        try {
            while(true) {
                ConsumerRecords<String, String> records = this.consumer.poll(1000);
                records.iterator().forEachRemaining(this::aggregate);
                this.producer.flush();
            }
        } finally {
            this.consumer.close();
            this.producer.close();
        }
    }

    private void aggregate(ConsumerRecord<String, String> record) {
        String sentence = record.value();
        Map<String, Long> countMap = countInSentence(sentence);
        countMap.forEach(this::produceResult);
    }

    private Map<String, Long> countInSentence(String sentence) {
        String[] words = sentence.split(" ");
        Stream<String> wordStream = StreamSupport.stream(
                Spliterators.spliterator(words, Spliterator.NONNULL), false);

        return wordStream.filter((word) -> word.length() > 0)
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private void produceResult(String word, long count) {
        ProducerRecord<String, String> record = new ProducerRecord<>(this.outTopic, word, Long.toString(count));
        this.producer.send(record);
    }

    public static void main(String[] args) {
        String inputTopic = args[0];
        String outputTopic = args[1];

        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        new WordCountMapper(inputTopic, outputTopic, groupId).run();
    }
}
