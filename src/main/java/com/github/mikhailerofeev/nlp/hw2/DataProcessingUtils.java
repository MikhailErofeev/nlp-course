package com.github.mikhailerofeev.nlp.hw2;

import com.github.mikhailerofeev.nlp.hw1.SentenceParser;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class DataProcessingUtils {

    public static Multimap<String, String> createLabelToTextsMap(Map<String, Set<String>> labels2Files, Set<String> niceWords) throws IOException {
        Multimap<String, String> label2Texts = HashMultimap.create();
        for (Map.Entry<String, Set<String>> label2Files : labels2Files.entrySet()) {
            for (String fileName : label2Files.getValue()) {
                File file = new File(fileName);
                final String text = FileUtils.readFileToString(file);
                final String s = normalizeSentences(text, niceWords).replaceAll("\n", " ");
                label2Texts.put(label2Files.getKey(), s);
            }
        }
        return label2Texts;
    }

    public static Multimap<String, String> createLabelToSentenceMap(Map<String, Set<String>> labels2Files, Set<String> niceWords) throws IOException {
        Multimap<String, String> label2Sentences = HashMultimap.create();
        for (Map.Entry<String, Set<String>> label2Files : labels2Files.entrySet()) {
            for (String fileName : label2Files.getValue()) {
                File file = new File(fileName);
                final List<String> sentences = SentenceParser.parseText(FileUtils.readFileToString(file)).getSentences();
                for (String sentence : sentences) {
                    sentence = sentence.replaceAll("\n", "").replaceAll("\t", "");
                    if (sentence.length() != 0) {
                        final String normalizedWords = normalizeAndFilterWords(sentence, niceWords);
                        label2Sentences.put(label2Files.getKey(), normalizedWords);
                    }
                }
            }
        }
        return label2Sentences;
    }

    private static String normalizeAndFilterWords(String sentence, Set<String> niceWords) {
        StringBuilder ret = new StringBuilder(sentence.length());
        for (String s : sentence.split("( |-)")) {
            s = normalizeWord(s);
            if (niceWords == null || niceWords.contains(s)) {
                ret.append(s).append(" ");
            }
        }
        if (ret.length() == 0) {
            return "";
        }
        return ret.subSequence(0, ret.length() - 1).toString();
    }

    public static String normalizeWord(String s) {
        return normalizeWord(s, 5);
    }

    public static String normalizeWord(String s, int minimalLengthToCat) {
        s = s.replaceAll("(«|»|-|'|,|–|\")", "");
        s = s.trim();
        s = s.toLowerCase();
        s = removePostfix(s, minimalLengthToCat);
        return s;
    }

    public static String removePostfix(String src) {
        return removePostfix(src, 5);
    }

    public static String removePostfix(String src, int minimalLength) {
        if (src.length() <= minimalLength) {
            return src;
        }
        final String dest = StringUtils.replacePattern(src,
                "(" + ADJECTIVE_ENDS + "|овал|овать|ьное|ования|ование|ства|ство|иями|ьюам|ием|" +
                        "ова|ову|оя|ием|ете|ться|тся|ель|ств|ами|" +
                        "ал|аю|ии|ые|ым|ых|ое|ий|ют|ин|ей|ям|ие|" +
                        "ер|ов|ет|ю|ся|ой|ую|ут|их|ит|ят|ия|ах|ок|ои|ем|ом|ов|" +
                        "у|о|я|ь|ы|е|а)$", ""
        );
        return dest;
    }

    private static final String LAST_NAME_ENDS = "ына|ова|ной|ную|ина|ев|ий|ым|им|ов|ир";

    private static final String ADJECTIVE_ENDS = "ующих|ующие|ующая|енного|енных|ельных|щихся|ельный|щейся|ного|" +
            "ите|иях|ный|ных|ный|ого|ому|его|ыми|" +
            "им|ый|ая|ей|ой|ая";

    private static final Pattern ADJECTIVE_PATTERN = Pattern.compile("(" + ADJECTIVE_ENDS + ")$");

    private static final Pattern LAST_NAME_PATTERN = Pattern.compile("(" + LAST_NAME_ENDS + ")$");

    public static boolean isAdjective(String word) {
        return ADJECTIVE_PATTERN.matcher(word).find();
    }


    public static boolean isPeople(String word) {
        return LAST_NAME_PATTERN.matcher(word).find();
    }

    private static String normalizeSentences(String text, Set<String> niceWords) {
        StringBuilder ret = new StringBuilder(text.length());
        for (String sentence : text.split("\n")) {
            String str = normalizeAndFilterWords(sentence, niceWords).replaceAll("\t", "");
            str = str.replaceAll(" +", " ");
            ret.append(str).append("\n");
        }
        return ret.subSequence(0, ret.length() - 1).toString();
    }

    public static Set<String> getNiceWords() throws IOException {
        Set<String> niceWords = Sets.newHashSet();
        for (Object strObj : FileUtils.readLines(new File("src/main/resources/niceAttrs.txt"))) {
            String s = ((String) strObj).trim();
            niceWords.add(s);
        }
        return niceWords;
    }
}
