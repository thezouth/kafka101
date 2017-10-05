package com.zouth.lab03;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WatsonFinder {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private String inputTopic;
    private String outputTopic;

    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    public WatsonFinder(String inputTopic, String outputTopic, Optional<String> groupId) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;

        this.consumer = createConsumer(groupId.orElse("find-watson"));
        this.producer = createProducer();
    }

    public void run() {
        this.consumer.subscribe(List.of(this.inputTopic));

        try {
            while(true) {
                ConsumerRecords<String, String> records = this.consumer.poll(1000);
                Stream<ConsumerRecord<String, String>> recordStream = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(records.iterator(), Spliterator.NONNULL),
                        false);

                recordStream.filter(this::filterWatson)
                            .forEach(this::produceOutput);

                this.producer.flush();
            }
        } finally {
            this.consumer.close();
            this.producer.close();
        }
    }


    private final static String[] possibleWatsons = {"Watson", "Doctor", "Dr", "John"};

    private boolean filterWatson(ConsumerRecord<String, String> record) {
        for (String watson : possibleWatsons) {
            if (record.value().contains(watson))
                return true;
        }

        return false;
    }

    private void produceOutput(ConsumerRecord<String, String> sentence) {
        this.producer.send(new ProducerRecord<>(
                this.outputTopic,
                sentence.partition(),
                sentence.timestamp(),
                sentence.key(),
                sentence.value(),
                null
        ));
    }

    public static void main(String[] args) {
        String inputTopic = args[0];
        String outputTopic = args[1];
        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        new WatsonFinder(inputTopic, outputTopic, groupId).run();
    }

    private KafkaConsumer<String, String> createConsumer(String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.0.44:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);

        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", false);

        return new KafkaConsumer<>(props);
    }

    public static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        props.put("lingers.ms", 50);
        props.put("batch.size", 1000);

        return new KafkaProducer<>(props);
    }

}
