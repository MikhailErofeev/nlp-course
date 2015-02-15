package com.github.mikhailerofeev.nlp.hw2;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DataNormalizersTest {

    @Test
    public void testRegex() {
        assertEquals("данн", DataNormalizers.removePostfix("данных"));
        assertEquals("данн", DataNormalizers.removePostfix("данные"));
        assertEquals("деградац", DataNormalizers.removePostfix("деградации"));
        assertEquals("деградац", DataNormalizers.removePostfix("деградациях"));
        assertEquals("исключен", DataNormalizers.removePostfix("исключением"));
        assertEquals("использ", DataNormalizers.removePostfix("использовать"));
        assertEquals("врем", DataNormalizers.removePostfix("временного"));
        assertEquals("друг", DataNormalizers.removePostfix("друга"));
        assertEquals("мир", DataNormalizers.removePostfix("мира"));
        assertEquals("мир", DataNormalizers.removePostfix("миров"));
        assertEquals("дизайн", DataNormalizers.removePostfix("дизайнер"));
        assertEquals("компан", DataNormalizers.removePostfix("компании"));
        assertEquals("компан", DataNormalizers.removePostfix("компаниями"));
        assertEquals("планет", DataNormalizers.removePostfix("планеты"));
        assertEquals("представит", DataNormalizers.removePostfix("представительный"));
        assertEquals("добровол", DataNormalizers.removePostfix("добровольное"));
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
        final String ret = DataNormalizers.normalizeWord(str);
        assertEquals("w", ret);
    }
}