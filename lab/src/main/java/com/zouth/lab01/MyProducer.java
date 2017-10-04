package com.zouth.lab01;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;


public class MyProducer {
    public static void main(String[] args) throws IOException {
        String topic = args[0];
        String target = args[1];

        KafkaProducer<String, String> producer = createProducer();

        readSource(target).forEach((sentence) -> {
            ProducerRecord<String, String> message = new ProducerRecord<>(topic, sentence);
            producer.send(message);
        });

        producer.flush();
        producer.close();
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

    static Stream<String> readSource(String sourceName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(sourceName);

        String text = new String(inputStream.readAllBytes());
        inputStream.close();

        return splitText(text);
    }

    static Stream<String> splitText(String text) {
        String noNewLine = text.replaceAll("\n", " ");
        return Arrays.stream(noNewLine.split("[.,?”“'`]"))
                .map(String::trim)
                .filter((sentence) -> sentence.length() > 0);
    }
}
