package com.github.mikhailerofeev.nlp.hw1;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 16.03.14
 */

public class Text {
    private final List<String> sentences;


    public Text(String... sentences) {
        this.sentences = Lists.newArrayList(sentences);
    }

    public void addSentence(String sentence) {
        sentences.add(sentence);
    }

    public List<String> getSentences() {
        return sentences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text)) return false;

        Text text = (Text) o;

        return sentences.equals(text.sentences);

    }

    @Override
    public int hashCode() {
        return sentences.hashCode();
    }

    public String joinSentencesWithNewLines() {
        if (getSentences().isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : getSentences()) {
            stringBuilder.append(s);
            final char last = s.charAt(s.length() - 1);
            if (last != '.' && last != '?' && last != '!') {
                stringBuilder.append("\n");
            }
            stringBuilder.append(" ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    //print sentence with uniq symbols, ignoring whitespaces
    //like 000000011111111112222222233333344445555555
    @Deprecated
    public String getSymbolicSentenceWithoutWhitespaces() {
        char symbol = 48;
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : getSentences()) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != ' ') {
                    stringBuilder.append(symbol);
                }
            }
            symbol++;
        }

        return stringBuilder.toString();
    }
}
