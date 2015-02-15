package com.github.mikhailerofeev.nlp.hw1;

import com.google.common.collect.Sets;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 13.05.14
 */
public class OpcorporaReader {

    private static final String PATH = "/Users/m-erofeev/projects/nlp/corpus/files/export/annot/annot.opcorpora.xml";

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        final Iterator<Text> textIterator = StructuredTextParser.parseFileIterable(new File(PATH));
        Text text;
        int i = 0;
        final boolean debug = false;
        StatisticsResult.Builder retBuilder = new StatisticsResult.Builder();
        while (textIterator.hasNext()) {
            text = textIterator.next();
            try {
                final StatisticsResult ret = processText(text, i, debug);
                retBuilder.addResult(ret);
            } catch (Exception e) {
                System.out.println(i);
                System.out.println(text.getSentences());
                throw e;
            }
        }
        System.out.println(retBuilder.build());
    }

    private static StatisticsResult processText(Text sourceText, int textNumber, boolean debug) {
        final String rawText = sourceText.joinSentencesWithNewLines();
        final Text restoredText = SentenceParser.parseText(rawText);
        final StatisticsResult stat = Statistics.calculate(sourceText, restoredText);
        if (debug && stat.getF1Measure() < 0.96) {
            printProblems(sourceText, textNumber, restoredText, stat);
        }
        return stat;
    }

    private static void printProblems(Text sourceText, int textNumber, Text restoredText, StatisticsResult stat) {
        Set<Integer> analysed = Sets.newHashSet(5, 17, 19, 20, 21, 23);
        if (analysed.contains(textNumber)) {
            return;
        }
        final int pos = ErrorsFinder.firstFailedSentence(sourceText, restoredText);
        System.out.println("F1 measure: " + stat.getF1Measure());
        System.out.println("expected: " + sourceText.getSentences().get(pos));
        System.out.println("restored: " + restoredText.getSentences().get(pos));
    }
}
