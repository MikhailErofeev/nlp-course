package com.github.mikhailerofeev.nlp.hw1;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author m-erofeev
 * @since 17.05.14
 */
public class HW1DataReader {

    public static void main(String[] args) throws IOException {
        final String dataPath = "../hw01_data";
        final boolean debug = false;
        File dir = new File(dataPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("bad dir for " + dataPath);
        }
        StatisticsResult.Builder resultStatisticBuilder = new StatisticsResult.Builder();

        //noinspection ConstantConditions
        for (File srcFile : dir.listFiles()) {
            final boolean isGoodFile = !srcFile.getName().contains("json") &&
                    !srcFile.getName().equals("hi-tech.parsed") &&
                    !srcFile.getName().equals("Тексты.txt");
            if (isGoodFile) {
                StatisticsResult statResult = calculateTextStatistic(srcFile, debug);
                resultStatisticBuilder.addResult(statResult);
            }
        }
        System.out.println(resultStatisticBuilder.build().toString());
    }

    private static StatisticsResult calculateTextStatistic(File srcFile, boolean debug) throws IOException {
        try {
            final String[] srcTextStr = FileUtils.readFileToString(srcFile).split("\n");
            Text srcText = new Text(srcTextStr);
            final String rawSentences = srcText.joinSentencesWithNewLines();
            final Text restoredText = SentenceParser.parseText(rawSentences);
            final StatisticsResult result = Statistics.calculate(srcText, restoredText);
            if (debug && result.getF1Measure() < 1) {
                final int pos = ErrorsFinder.firstFailedSentence(srcText, restoredText);
                System.out.println(srcFile.getName());
                System.out.println("expected: " + srcText.getSentences().get(pos));
                System.out.println("restored: " + restoredText.getSentences().get(pos));
            }
            return result;
        } catch (Exception e) {
            System.err.println(srcFile.getName());
            throw e;
        }
    }
}
