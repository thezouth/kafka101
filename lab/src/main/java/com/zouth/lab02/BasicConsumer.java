package com.zouth.lab02;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;


public class BasicConsumer {

    public static void main(String[] args) throws IOException {
        String topic = args[0];
        String path = args[1];
        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        BasicConsumer myConsumer = new BasicConsumer(topic, path, groupId);
        myConsumer.run();
    }

    public BasicConsumer(String topic, String outputPath, Optional<String> groupId)
            throws IOException {
    }

    public void run() throws IOException {
        throw new UnsupportedOperationException("Listen to Kafka, and log the result");

    }

    private final static ZoneId BKK_ZONE = ZoneId.of("Asia/Bangkok");
    private String formatTimestamp(long epochTimestamp) {
        return Instant.ofEpochMilli(epochTimestamp)
                .atZone(BKK_ZONE)
                .toString();
    }
}

