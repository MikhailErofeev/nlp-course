package com.github.mikhailerofeev.nlp.hw1;

/**
 * @author m-erofeev
 * @since 13.05.14
 */
public class ErrorsFinder {

    public static Integer firstFailedSentence(Text first, Text second) {
        for (int ret = 0; ret < first.getSentences().size(); ret++) {
            final String firstSentence = first.getSentences().get(ret);
            final String secondSentence = second.getSentences().get(ret);
            if (!firstSentence.equals(secondSentence)) {
                return ret;
            }
        }
        return null;
    }
}
