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

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class DataNormalizers {


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
                "(ующих|енного|енных|ельных|овал|овать|ьное|ования|ование|щихся|ельный|ства|ство|иями|щейся|ьюам|" +
                        "ова|ову|оя|ием|ете|ите|иях|ный|ого|ами|ного|ться|тся|ель|его|ств|" +
                        "ие|им|ый|ая|ал|аю|ии|ые|ым|ых|ое|ий|ют|ей|ин|ей|ям|" +
                        "ер|ов|ов|ет|ем|ом|ов|ю|ся|ой|ую|ут|их|ит|ой|ят|ия|ах|ок|ои|" +
                        "у|о|я|ь|ы|е|а)$", ""
        );
        return dest;
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
