package com.zouth.lab01;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class MyProducerTest {
    @Test
    @DisplayName("Split paragraph to sentence by `.,?”'`")
    public void paragraphToSentenceWithDot() {
        String paragraph = "You make too much of a trifle,” said I. “May I ask\n" +
                "how you knew who I was?”";

        List<String> expected = List.of(
                "You make too much of a trifle",
                "said I",
                "May I ask how you knew who I was"
        );

        List<String> actual = MyProducer.splitText(paragraph).collect(Collectors.toList());

        Assertions.assertEquals(expected, actual);
    }
}
