package com.zouth.lab02;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BasicConsumer {
    private static Logger logger = Logger.getLogger(BasicConsumer.class.getName());
    private String topic;

    private BufferedWriter outputWriter;
    private KafkaConsumer<String, String> consumer;

    public BasicConsumer(String topic, String outputPath, Optional<String> groupId) throws IOException {
        this.topic = topic;

        Path output = Paths.get(outputPath);
        outputWriter = Files.newBufferedWriter(output, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

        String groupIdValue = groupId.orElse("file-writer");
        consumer = createConsumer(groupIdValue);
    }

    public void run() throws IOException {
        logger.info("Listen to " + this.topic);
        this.consumer.subscribe(List.of(this.topic));

        logger.info("Start write a file.");
        try {
            while (true) {
                ConsumerRecords<String, String> records = this.consumer.poll(1000);
                Stream<ConsumerRecord<String, String>> recordStream = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(records.iterator(), Spliterator.ORDERED),
                        false);

                recordStream.map(this::formatMessage)
                            .forEach(this::writeMessage);

                this.outputWriter.flush();
                this.consumer.commitSync();
            }
        } finally {
            logger.info("Process done, closing all Closable");

            this.outputWriter.close();
            this.consumer.close();

            logger.info("Ready to be terminated.");
        }

    }

    public static void main(String[] args) throws IOException {
        String topic = args[0];
        String path = args[1];
        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        BasicConsumer myConsumer = new BasicConsumer(topic, path, groupId);
        myConsumer.run();
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

    private void writeMessage(String message) {
        try {
            this.outputWriter.write(message);
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    private String formatMessage(ConsumerRecord<String, String> record) {
        return formatTimestamp(record.timestamp())
                + " | " + record.partition()
                + " | " + record.value() + "\n";
    }

    private final static ZoneId BKK_ZONE = ZoneId.of("Asia/Bangkok");
    private String formatTimestamp(long epochTimestamp) {
        return Instant.ofEpochMilli(epochTimestamp)
                .atZone(BKK_ZONE)
                .toString();
    }
}

