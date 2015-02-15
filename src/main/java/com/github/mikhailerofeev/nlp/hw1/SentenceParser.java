package com.github.mikhailerofeev.nlp.hw1;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 16.03.14
 */
public class SentenceParser {
    private final static Pattern DELIMITER_PATTERN = Pattern.compile("[.?!]+(?=($| ([A-ZА-Я\"'„§<•№«—(]|\\d)))"); //@todo special symbol?
    private final static Pattern ABBR_PATTERN = Pattern.compile("^[A-ZА-Я\\.]+$");
    private final static Pattern IO_PATTERN = Pattern.compile("^([A-ZА-Я]([а-яa-z])?\\.)+$");
    private final static Pattern PARAGRAPH_NUMBER_PATTERN = Pattern.compile("^(§)?[\\d\\.]+$");
    private final static Set<String> ABBRS = Sets.newHashSet("стр", "тов", "ст", "ч", "им", "п", "г", "см", "Corp", "рис", "гор", "лат", "нач");

    public static Text parseText(String text) {
        final Text ret = new Text();
        for (String paragraph : Splitter.on('\n').omitEmptyStrings().split(text)) {
            parseParagraph(ret, paragraph.trim());
        }
        return ret;
    }

    private static void parseParagraph(final Text ret, final String paragraph) {
        Matcher matcher = DELIMITER_PATTERN.matcher(paragraph);
        int lastSentenceEnd = 0;
        while (matcher.find()) {
            final int startOfDelimeter = matcher.start();
            final int endOfDelimeter = matcher.end();
            final String delimiterTrimmed = matcher.group().trim();
            final String candidateWithWhitespace = paragraph.substring(lastSentenceEnd, startOfDelimeter);
            final String sentenceCandidate = StringUtils.stripStart(candidateWithWhitespace, " ");
            final String partWithDelimiter = sentenceCandidate + delimiterTrimmed;
            boolean abbr = ABBR_PATTERN.matcher(sentenceCandidate).find();
            boolean paragraphNumber = PARAGRAPH_NUMBER_PATTERN.matcher(sentenceCandidate).find();
            final boolean isEndOfSentence = !abbr
                    && !paragraphNumber
                    && !endWithIncorrectAbbr(sentenceCandidate, delimiterTrimmed)
                    && !hasUnclosedBrackets(sentenceCandidate)
                    && !hasUnclosedQuotasInside(sentenceCandidate);
            if (isEndOfSentence) {
                ret.addSentence(partWithDelimiter);
                lastSentenceEnd = endOfDelimeter;
            }
        }
        final String lastPath = paragraph.substring(lastSentenceEnd, paragraph.length()).trim();
        if (!lastPath.isEmpty()) {
            ret.addSentence(lastPath);
        }
    }

    //too noisy
    private static boolean hasUnclosedQuotasInside(String sentenceCandidate) {
        return false;
//        final int close = StringUtils.countMatches(sentenceCandidate, "»");
//        final int open = StringUtils.countMatches(sentenceCandidate, "«");
//        return close < open //expecting some texts without closed
//                && !sentenceCandidate.startsWith("«");  //quota with many sentences (not 100%)
//        @TODO stat to != comparsion
    }

    //by syntax, abbrs should not have whitspaces after it
    private static boolean endWithIncorrectAbbr(String sentenceCandidate, String delimeter) {
        final int i = sentenceCandidate.lastIndexOf(' ');
        if (i == -1) {
            return false;
        } else {
            String lastWord = sentenceCandidate.substring(i + 1);
            final boolean definedAbbrs = ABBRS.contains(lastWord);
            if (definedAbbrs) {
                return true;
            } else if (lastWord.length() == 1 && Character.isUpperCase(lastWord.charAt(0))) {
                return true;
            } else {
                final String input = lastWord + delimeter;
                final boolean io = IO_PATTERN.matcher(input).find();
                return io;
            }
        }
    }

    private static boolean hasUnclosedBrackets(String sentenceCandidate) {
        final int close = StringUtils.countMatches(sentenceCandidate, ")");
        final int open = StringUtils.countMatches(sentenceCandidate, "(");
        return close < open; //1) bla-bla 2) tra-tra 3) piu-piu
    }
}
