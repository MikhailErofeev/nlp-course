package com.github.mikhailerofeev.nlp.hw5;

import com.github.mikhailerofeev.nlp.hw1.StatisticsResult;
import com.github.mikhailerofeev.nlp.hw3.NaiveBayesClassifier;
import com.github.mikhailerofeev.nlp.hw3.NaiveBayesClassifierUtils;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 22.06.14
 */
public class Classifier {

    private static final String positiveLearnTweets = "src/main/resources/hw5/smile-pos.txt";
    private static final String negativeLearnTweets = "src/main/resources/hw5/smile-neg.txt";

    private static final String positiveTestTweets = "src/main/resources/hw5/google-pos.txt";
    private static final String negativeTestTweets = "src/main/resources/hw5/google-pos.txt";

    private static final String mostPositiveWords = "src/main/resources/hw5/most-pos-words.txt";
    private static final String mostNegativeWords = "src/main/resources/hw5/most-neg-words.txt";

    public static void main(String[] args) throws IOException {
        List<String> positive = getLines(positiveLearnTweets);
        List<String> negative = getLines(negativeLearnTweets);
        int minimalWordOccurence = 4;
        Set<String> positiveWords = Sets.newHashSet(getLines(mostPositiveWords));
        Set<String> negativeWords = Sets.newHashSet(getLines(mostNegativeWords));
        NaiveBayesClassifier classifier =
                NaiveBayesClassifierUtils.buildClassifier(positive, negative, minimalWordOccurence, positiveWords, negativeWords);
        StatisticsResult learnBuild = getStat(positive, negative, classifier);
        System.out.println("learn " + learnBuild);
        StatisticsResult testBuild = getStat(getLines(positiveTestTweets), getLines(negativeTestTweets), classifier);
        System.out.println("test " + testBuild);
//        new ChiSquareTest(positive, negative, classifier).calculate(minimalWordOccurence);
    }

    private static StatisticsResult getStat(List<String> positive, List<String> negative, NaiveBayesClassifier classifier) {
        StatisticsResult.Builder statBuilder = new StatisticsResult.Builder();
        for (String s : positive) {
            double pos = classifier.getChanceForText(s);
            if (pos >= 1) {
                statBuilder.addTp(1);
            } else {
                statBuilder.addFn(1);
            }
        }
        for (String s : negative) {
            double pos = classifier.getChanceForText(s);
            if (pos < 1) {
                statBuilder.addTn(1);
            } else {
                statBuilder.addFp(1);
            }
        }
        return statBuilder.build();
    }

    private static List getLines(String positiveLearnTweets1) throws IOException {
        return FileUtils.readLines(new File(positiveLearnTweets1));
    }
}
