package com.zouth.lab03;


import com.zouth.KafkaUtil;
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

        this.consumer = KafkaUtil.createConsumer(groupId.orElse("find-watson"));
        this.producer = KafkaUtil.createProducer();
    }

    public void run() {
        logger.info("Listen to " + this.inputTopic);
        this.consumer.subscribe(List.of(this.inputTopic));
        logger.info("Start finding Watson");

        try {
            while(true) {
                ConsumerRecords<String, String> records = this.consumer.poll(1000);

                records.iterator().forEachRemaining((record) -> {
                    if (this.filterWatson(record))
                        this.produceOutput(record);
                });

                this.producer.flush();
            }
        } finally {
            logger.info("Process done, closing all Closable");
            this.consumer.close();
            this.producer.close();
            logger.info("Ready to be terminated.");
        }
    }


    private final static String[] possibleWatson = {"Watson", "Doctor", "Dr", "John"};

    private boolean filterWatson(ConsumerRecord<String, String> record) {
        for (String watson : possibleWatson) {
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

}
