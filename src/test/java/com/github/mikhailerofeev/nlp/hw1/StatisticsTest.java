package com.github.mikhailerofeev.nlp.hw1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class StatisticsTest {

    @Test
    public void testStatistics() throws FileNotFoundException, XMLStreamException {
        File file = new File("src/test/resources/corpus/annot.opcorpora/id2.xml");
        final Text text = StructuredTextParser.parseFile(file).get(0);
        assertEquals(44, text.getSentences().size());
        final String rawText = text.joinSentencesWithNewLines();
        final Text restoredText = SentenceParser.parseText(rawText);
        assertEquals(restoredText.getSentences(), text.getSentences());
        final StatisticsResult result = Statistics.calculate(text, restoredText);
        assertEquals(1.0, result.getPrecision());
        assertEquals(1.0, result.getRecall());
        assertEquals(1.0, result.getF1Measure());
        assertEquals(1.0, result.getAccuracy());

    }

    @Test
    public void testStatisticsSimple() {
        final Text text = new Text("привет, тов. 333 ide.", "как дела?1");
        final Text alt = new Text("привет, тов. 333", "ide.", "как дела?1");
        final StatisticsResult result = Statistics.calculate(text, alt);
        assertEquals(9, result.getTp());
        assertEquals(0, result.getFn());
        assertEquals(18, result.getFp());
        assertEquals(1.0 / 3, result.getPrecision());
        assertEquals(1.0, result.getRecall());
        assertEquals(0.5, result.getF1Measure());
        assertEquals(1.0 / 3, result.getAccuracy());
    }

    @Test
    public void testStatisticsSimple2() {
        final Text text = new Text("привет, тов. 333", "ide. ", "как дела?1");
        final Text alt = new Text("привет, тов. 333 ide. ", "как дела?1");
        final StatisticsResult result = Statistics.calculate(text, alt);
        assertEquals(9, result.getTp());
        assertEquals(18, result.getFn());
        assertEquals(0, result.getFp());
        assertEquals(1.0, result.getPrecision());
        assertEquals(1.0 / 3, result.getRecall());
        assertEquals(0.5, result.getF1Measure());
        assertEquals(1.0 / 3, result.getAccuracy());
    }

    @Test
    public void testStatisticsEquals() {
        final Text text = new Text("привет, тов. ide. ", "как дела?");
        final Text alt = new Text("привет, тов. ide. ", "как дела?");
        final StatisticsResult result = Statistics.calculate(text, alt);
        assertEquals(23, result.getTp());
        assertEquals(0, result.getFn());
        assertEquals(0, result.getFp());
        assertEquals(1.0, result.getPrecision());
        assertEquals(1.0, result.getRecall());
        assertEquals(1.0, result.getF1Measure());
        assertEquals(1.0, result.getAccuracy());
    }

    @Test
    public void testEmpty() {
        final Text text = new Text();
        final Text alt = SentenceParser.parseText(text.joinSentencesWithNewLines());
        final StatisticsResult result = Statistics.calculate(text, alt);
        assertEquals(0, result.getTp());
        assertEquals(0, result.getFn());
        assertEquals(0, result.getFp());
        assertEquals(1.0, result.getPrecision());
        assertEquals(1.0, result.getRecall());
        assertEquals(1.0, result.getF1Measure());
        assertEquals(1.0, result.getAccuracy());
    }

    @Test
    public void testId5() throws FileNotFoundException, XMLStreamException {
        final File file = new File("src/test/resources/corpus/annot.opcorpora/id5.xml");
        final Text text = StructuredTextParser.parseFile(file).get(0);
        final String rawText = text.joinSentencesWithNewLines();
        final Text alt = SentenceParser.parseText(rawText);
        final String rawTextRestored = alt.joinSentencesWithNewLines();
        assertEquals(rawText, rawTextRestored);
        final Integer pos = ErrorsFinder.firstFailedSentence(text, alt);
        if (pos != null) {
            System.out.println(text.getSentences().get(pos));
            System.out.println(alt.getSentences().get(pos));
        }
        final StatisticsResult result = Statistics.calculate(text, alt);
        assertEquals(6518, result.getTp());
        assertEquals(0, result.getFn());
        assertEquals(0, result.getFp());
        assertEquals(1.0, result.getPrecision());
        assertEquals(1.0, result.getRecall());
        assertEquals(1.0, result.getF1Measure());
        assertEquals(1.0, result.getAccuracy());
    }
}