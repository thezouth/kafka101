package com.zouth.lab04;

import com.zouth.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;
import java.util.stream.StreamSupport;

public class WordCountReducer {
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    private String inTopic, outTopic;

    private Map<String, Long> countState = new HashMap<>();

    public WordCountReducer(String inTopic, String outTopic, Optional<String> groupId) {
        this.inTopic = inTopic;
        this.outTopic = outTopic;

        this.consumer = KafkaUtil.createConsumer(groupId.orElse("word-count-reducer"));
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
        String word = record.key();
        long count = Long.parseLong(record.value());
        long result = countState.merge(word, count, (oldValue, newValue) -> newValue + oldValue);

        this.produceResult(word, result);
    }

    private void produceResult(String word, long count) {
        ProducerRecord<String, String> record = new ProducerRecord<>(this.outTopic, word, Long.toString(count));
        this.producer.send(record);
    }

    public static void main(String[] args) {
        String inputTopic = args[0];
        String outputTopic = args[1];

        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        new WordCountReducer(inputTopic, outputTopic, groupId).run();
    }
}
