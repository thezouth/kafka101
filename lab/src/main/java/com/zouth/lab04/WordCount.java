package com.zouth.lab04;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCount {
    private Map<String, Long> countState = new HashMap<>();

    public Map<String, Long> count(String sentence) {
        String[] words = sentence.split(" ");
        Stream<String> wordStream = StreamSupport.stream(
                Spliterators.spliterator(words, Spliterator.NONNULL), false);

        Map<String, Long> sentenceCount = countInSentence(wordStream);
        return mergeWithState(sentenceCount);
    }

    private Map<String, Long> countInSentence(Stream<String> wordStream) {
        return wordStream.filter((word) -> word.length() > 0)
                         .map(String::toLowerCase)
                         .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Map<String, Long> mergeWithState(Map<String, Long> localCount) {
        Map<String, Long> updatedCount =
                localCount.entrySet().stream()
                        .map((val) -> Map.entry(val.getKey(),
                                countState.getOrDefault(val.getKey(), 0L) + val.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        countState.putAll(updatedCount);

        return countState;
    }

}
