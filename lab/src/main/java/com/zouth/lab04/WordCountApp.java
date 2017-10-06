package com.zouth.lab04;

import java.util.Optional;

public class WordCountApp {

    public WordCountApp(String inTopic, String outTopic, Optional<String> groupId) {
    }

    public void run() {
        throw new UnsupportedOperationException("Implement here.");
    }

    public static void main(String[] args) {
        final String inputTopic = args[0];
        final String outputTopic = args[1];
        final Optional<String> groupId = args.length >= 3 ? Optional.of(args[2]) : Optional.empty();

        new WordCountApp(inputTopic, outputTopic, groupId).run();
    }
}
