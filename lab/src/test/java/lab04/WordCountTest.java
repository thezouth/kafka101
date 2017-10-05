package lab04;

import com.zouth.lab04.WordCount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class WordCountTest {
    @Test
    public void initialCount() {
        String sentence = "A man who fall in a hole";
        Map<String, Long> actual = new WordCount().count(sentence);

        Map<String, Long> expected = Map.of(
                "a", 2L,
                "man", 1L,
                "who", 1L,
                "fall", 1L,
                "in", 1L,
                "hole", 1L
        );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void streamCount() {
        WordCount counter = new WordCount();

        counter.count("A man who fall in a hole");

        Map<String, Long> actual = counter.count("A man who fall in a hole");

        Map<String, Long> expected = Map.of(
                "a", 4L,
                "man", 2L,
                "who", 2L,
                "fall", 2L,
                "in", 2L,
                "hole", 2L
        );

        Assertions.assertEquals(expected, actual);
    }
}
