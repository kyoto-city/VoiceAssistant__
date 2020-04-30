package com.example.voiceassistant2;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {
        ParsingHtmlService phs = new ParsingHtmlService();
        assertEquals("Новый год", phs.getHoliday("1 января 2020"));
    }
}