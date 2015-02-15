package com.github.mikhailerofeev.nlp.hw3;

import com.github.mikhailerofeev.nlp.hw2.DataNormalizers;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class NaiveBayesClassifier {
    private final double classChance;
    private final Map<String, Double> word2classChance;


    NaiveBayesClassifier(double classChance, Map<String, Double> word2classChance) {
        this.classChance = classChance;
        this.word2classChance = word2classChance;
    }

    public double getClassChance() {
        return classChance;
    }

    public Map<String, Double> getWord2classChance() {
        return word2classChance;
    }

    public double getChanceForText(String text) {
        List<String> words = Lists.newArrayList(text.split("( |-)"));
        words = Lists.transform(words, new Function<String, String>() {
            @Override
            public String apply(String s) {
                final String s1 = DataNormalizers.normalizeWord(s);
                return s1;
            }
        });
        words.remove("");
        double chance = classChance;
        for (String word : words) {
            Double wordCost = Optional.fromNullable(word2classChance.get(word)).or(1.0);
            chance *= wordCost;
        }
        double notClassChance = 1 - classChance;
        for (String word : words) {
            notClassChance *= 1 - Optional.fromNullable(word2classChance.get(word)).or(0.0);
        }
        if (notClassChance == 0) {
            return 1.01;
        } else {
            double v = chance / notClassChance;
            return v;
        }
    }
}
