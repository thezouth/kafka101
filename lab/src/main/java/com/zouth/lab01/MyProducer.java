package com.zouth.lab01;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Stream;


public class MyProducer {
    public static void main(String[] args) throws IOException {
        throw new UnsupportedOperationException("D.I.Y. Method");
    }

    static Stream<String> readSource(String sourceName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(sourceName);

        String text = new String(inputStream.readAllBytes());
        inputStream.close();

        return splitText(text);
    }

    static Stream<String> splitText(String text) {
        String noNewLine = text.replaceAll("\n", " ")
                               .replaceAll(",", "");
        return Arrays.stream(noNewLine.split("[.?”“'`]"))
                .map(String::trim)
                .filter((sentence) -> sentence.length() > 0);
    }
}
