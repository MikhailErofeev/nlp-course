package com.github.mikhailerofeev.nlp.hw2;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DataProcessingUtilsTest {

    @Test
    public void testRegex() {
        assertEquals("данн", DataProcessingUtils.removePostfix("данных"));
        assertEquals("данн", DataProcessingUtils.removePostfix("данные"));
        assertEquals("деградац", DataProcessingUtils.removePostfix("деградации"));
        assertEquals("деградац", DataProcessingUtils.removePostfix("деградациях"));
        assertEquals("исключен", DataProcessingUtils.removePostfix("исключением"));
        assertEquals("использ", DataProcessingUtils.removePostfix("использовать"));
        assertEquals("врем", DataProcessingUtils.removePostfix("временного"));
        assertEquals("друг", DataProcessingUtils.removePostfix("друга"));
        assertEquals("мир", DataProcessingUtils.removePostfix("мира"));
        assertEquals("мир", DataProcessingUtils.removePostfix("миров"));
        assertEquals("дизайн", DataProcessingUtils.removePostfix("дизайнер"));
        assertEquals("компан", DataProcessingUtils.removePostfix("компании"));
        assertEquals("компан", DataProcessingUtils.removePostfix("компаниями"));
        assertEquals("планет", DataProcessingUtils.removePostfix("планеты"));
        assertEquals("представит", DataProcessingUtils.removePostfix("представительный"));
        assertEquals("добровол", DataProcessingUtils.removePostfix("добровольное"));
    }

    @Test
    public void testWhiteSpaces() {
        String str = "first      second  third         fourth";
        str = str.replaceAll(" +", " ");
        assertEquals("first second third fourth", str);
    }

    @Test
    public void tesBadSymbols() {
        String str = "w-";
        final String ret = DataProcessingUtils.normalizeWord(str);
        assertEquals("w", ret);
    }
}