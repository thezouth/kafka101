package com.zouth.lab03;

import java.util.*;


public class WatsonFinder {
    public WatsonFinder(String inputTopic, String outputTopic, Optional<String> groupId) {
    }

    public void run() {
        throw new UnsupportedOperationException("Listen to Kafka, and find Watson.");
    }

    public static void main(String[] args) {
        String inputTopic = args[0];
        String outputTopic = args[1];
        Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        new WatsonFinder(inputTopic, outputTopic, groupId).run();
    }

}
