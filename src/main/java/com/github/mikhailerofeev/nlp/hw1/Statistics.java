package com.github.mikhailerofeev.nlp.hw1;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 05.04.14
 */
public class Statistics {
  /*
   * precision  = tp/(tp + fp)
   * recall = tp/(tp+fn)
   * F1 = 2 * PR/(P+R)
   * accuracy = (tp + tn)/(tp + fp + fn + tn) 
   * 
   * tp - правильное разбитое преложение 
   * fn - неправильное (отуствующее) предложение
   * fp - неправильное (лишнее) предложение
   * tn - правильное отсуствие предожения (не может быть)
   *   
   * _____. ______? ______!
   * _____________? ______!
   * ffffffffffffff ttttttt 
   * nnnnnnnnnnnnnn ppppppp
   * 
   * _____! _____________? ______.
   * _____! ______. _____? ______.
   * tttttt ffffffffffffff ttttttt
   * pppppp pppppppppppppp ppppppp
   * 
   * 
   * __________._________? ______.
   * _____! ______. _____? ______.
   * ffffff ffffffffffffff ttttttt
   * nnnnnn nnnnnnnnnnnnnn ppppppp
   */

    public static StatisticsResult calculate(Text expected, Text actual) {
        int textPos = 0;
        int tp = 0;
        int fp = 0;
        int fn = 0;
        final String actualSymbolicSentence = actual.getSymbolicSentenceWithoutWhitespaces();
        final List<String> expectedSentences = expected.getSentences();
        for (int i = 0; i < expectedSentences.size(); i++) {
            String s = StringUtils.remove(expected.getSentences().get(i), ' ');
            if (isFullSentence(actualSymbolicSentence, textPos, s.length())) {
                tp += s.length();
            } else if (isManyFullSentences(actualSymbolicSentence, textPos, s.length())) {
                fp += s.length();
            } else {
                fn += s.length();
            }
            textPos += s.length();
        }
        return new StatisticsResult.Builder().setTp(tp).setFp(fp).setFn(fn).build();
    }

    private static boolean isManyFullSentences(String actualSymbolicSentence, int textPos, int length) {
        final char firstChar = actualSymbolicSentence.charAt(textPos);
        char lastChar = firstChar;
        final int nextSentenceStart = textPos + length;
        for (int i = textPos + 1; i < nextSentenceStart; i++) {
            if (i < actualSymbolicSentence.length() && actualSymbolicSentence.charAt(i) != lastChar) {
                lastChar = actualSymbolicSentence.charAt(i);
            }
        }
        if (firstChar == lastChar) {
            return false;
        } else if (textPos > 0 && actualSymbolicSentence.charAt(textPos - 1) == firstChar) {
            return false;
        } else if (nextSentenceStart < actualSymbolicSentence.length()
                && actualSymbolicSentence.charAt(nextSentenceStart) == lastChar) {
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isFullSentence(String actualSymbolicSentence, int textPos, int length) {
        char ch = actualSymbolicSentence.charAt(textPos);
        final int nextSentenceStart = textPos + length;
        for (int i = textPos + 1; i < nextSentenceStart; i++) {
            if (actualSymbolicSentence.charAt(i) != ch) {
                return false;
            }
        }
        if (textPos > 0 && actualSymbolicSentence.charAt(textPos - 1) == ch) {
            return false;
        } else if (nextSentenceStart < actualSymbolicSentence.length()
                && actualSymbolicSentence.charAt(nextSentenceStart) == ch) {
            return false;
        } else {
            return true;
        }
    }
}
