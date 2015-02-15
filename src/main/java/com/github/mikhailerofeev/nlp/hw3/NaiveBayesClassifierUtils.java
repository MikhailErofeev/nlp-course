package com.github.mikhailerofeev.nlp.hw3;

import com.github.mikhailerofeev.nlp.hw1.StatisticsResult;
import com.github.mikhailerofeev.nlp.hw2.DataNormalizers;
import com.github.mikhailerofeev.nlp.hw2.TextsFinder;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class NaiveBayesClassifierUtils {
    private final static String basePath = "src/main/resources/hw3/";
    private final static File positiveLearn = new File(basePath + "positive-texts-learn.txt");
    private final static File negativeLearn = new File(basePath + "negative-texts-learn.txt");
    private final static File positiveTest = new File(basePath + "positive-texts-test.txt");
    private final static File negativeTest = new File(basePath + "negative-texts-test.txt");

    public static void main(String[] args) throws IOException {
        testOnAllLabels();
    }

    private static void testOnAllLabels() throws IOException {
        StatisticsResult.Builder overallBuilder = new StatisticsResult.Builder();
        for (String label : TextsFinder.LABELS_2_RUSSIAN_NAMES.keySet()) {

            StatisticsResult.Builder builder = new StatisticsResult.Builder();
            for (int i = 0; i < 1; i++) {
                fillFilesWithPositiveLabel(label, false, DataNormalizers.getNiceWords());
                final StatisticsResult statistics = generateStatistics(0);
                builder.addResult(statistics);
            }
            final StatisticsResult labelResult = builder.build();
            overallBuilder.addResult(labelResult);
            System.out.println(label + "\t" + labelResult);
        }
        System.out.println("overall:" + "\t" + overallBuilder.build());
    }

    private static StatisticsResult generateStatistics(int minimalWordOccurences) throws IOException {
        final String[] positiveTexts = FileUtils.readFileToString(positiveLearn).split("\n");
        final String[] negativeTexts = FileUtils.readFileToString(negativeLearn).split("\n");
        final NaiveBayesClassifier bayesClassifier = buildClassifier(positiveTexts, negativeTexts, minimalWordOccurences);

        final String[] positiveTestTexts = FileUtils.readFileToString(positiveTest).split("\n");
        final String[] negativeTestTexts = FileUtils.readFileToString(negativeTest).split("\n");

        StatisticsResult.Builder statBuilder = new StatisticsResult.Builder();
        for (String text : positiveTestTexts) {
            final double chance = bayesClassifier.getChanceForText(text);
//            System.out.println(chance + "\t" + text);
            if (chance >= 0.5) {
                statBuilder.addTp(1);
            } else {
                statBuilder.addFp(1);
            }
        }

        for (String text : negativeTestTexts) {
            final double chance = bayesClassifier.getChanceForText(text);
            if (chance < 0.5) {
                statBuilder.addTn(1);
            } else {
//                System.out.println(chance + "\t" + text);
                statBuilder.addFn(1);
            }
        }
        return statBuilder.build();
    }

    public static NaiveBayesClassifier buildClassifier(String[] positiveTexts, String[] negativeTexts, int minimalWordOccurrences) throws IOException {
        List<String> positiveTextsList = Lists.newArrayList(positiveTexts);
        List<String> negativeTextsLists = Lists.newArrayList(negativeTexts);
        return buildClassifier(positiveTextsList, negativeTextsLists, minimalWordOccurrences, null, null);
    }

    public static NaiveBayesClassifier buildClassifier(List<String> positiveTextsList,
                                                       List<String> negativeTextsLists,
                                                       int minimalWordOccurrences,
                                                       final Set<String> mostPositiveWords,
                                                       final Set<String> mostNegativeWords) {
        double classChance = (double) positiveTextsList.size() / (positiveTextsList.size() + negativeTextsLists.size());
        Map<String, Integer> positiveWords = wordToCount(positiveTextsList);
        Map<String, Integer> negativeWords = wordToCount(negativeTextsLists);
        if (mostNegativeWords != null) {
            negativeWords = Maps.filterEntries(negativeWords, new Predicate<Map.Entry<String, Integer>>() {
                @Override
                public boolean apply(Map.Entry<String, Integer> stringIntegerEntry) {
                    return mostNegativeWords.contains(stringIntegerEntry.getKey());
                }
            });
        }
        if (mostPositiveWords != null) {
            positiveWords = Maps.filterEntries(positiveWords, new Predicate<Map.Entry<String, Integer>>() {
                @Override
                public boolean apply(Map.Entry<String, Integer> stringIntegerEntry) {
                    return mostPositiveWords.contains(stringIntegerEntry.getKey());
                }
            });
        }
        final Map<String, Double> word2classChance = Maps.newHashMap();
        for (Map.Entry<String, Integer> word2PositiveCount : positiveWords.entrySet()) {
            final int positiveCount = word2PositiveCount.getValue();
            if (positiveCount < minimalWordOccurrences) {
                continue;
            }
            final String word = word2PositiveCount.getKey();
            final int negativeCount = Optional.fromNullable(negativeWords.get(word)).or(0);
            final double percent = (double) positiveCount / (positiveCount + negativeCount);
            word2classChance.put(word, percent);
        }
        for (Map.Entry<String, Integer> word2negativeCount : negativeWords.entrySet()) {
            final String word = word2negativeCount.getKey();
            final int count = word2negativeCount.getValue();
            if (count < minimalWordOccurrences) {
                continue;
            }
            final boolean isNegative = !positiveWords.containsKey(word);
            if (isNegative) {
                word2classChance.put(word, 0.0);
            }
        }
        return new NaiveBayesClassifier(classChance, word2classChance);
    }

    private static void fillFilesWithPositiveLabel(String positiveLabel, boolean onlyMyTexts, Set<String> niceWords) throws IOException {
        final Map<String, Set<String>> label2FilePath = TextsFinder.fillInFileNames(onlyMyTexts);
        final Multimap<String, String> labelToTextsMap = DataNormalizers.createLabelToTextsMap(label2FilePath, niceWords);

        List<String> positive = Lists.newArrayList();
        List<String> negative = Lists.newArrayList();
        fillInPositiveAndNegative(labelToTextsMap, positiveLabel, positive, negative);

        writeLearnTest(positive, positiveLearn, positiveTest);
        writeLearnTest(negative, negativeLearn, negativeTest);
    }

    public static Map<String, Integer> wordToCount(List<String> classTexts) {
        Map<String, Integer> word2count = Maps.newHashMap();
        for (String classText : classTexts) {
            final List<String> words = splitTextToWordsAndNormalize(classText);
            for (String word : words) {
                int count = Optional.fromNullable(word2count.get(word)).or(0);
                word2count.put(word, count + 1);
            }
        }
        return word2count;

    }


    private static final Pattern TWITTER_NAME_PATTERN = Pattern.compile("@[a-z_]+");

    public static List<String> splitTextToWordsAndNormalize(String classText) {
        List<String> ret = Lists.newArrayList();
        String[] split = classText.split("( |-)"); //todo -- split smiles
        for (String s : split) {
            s = DataNormalizers.normalizeWord(s, 3);
            if (!StringUtils.isBlank(s)) {
                boolean isLogin = TWITTER_NAME_PATTERN.matcher(s).find();
                if (!isLogin) {
                    ret.add(s);
                }
            }
        }
        return ret;
    }

    private static void writeLearnTest(List<String> classTexts, File learn, File test) throws IOException {
        FileUtils.writeStringToFile(learn, ""); // create/clean
        FileUtils.writeStringToFile(test, ""); // create/clean
        PrintWriter learnWriter = new PrintWriter(learn);
        PrintWriter testWriter = new PrintWriter(test);
        for (int i = 0; i < classTexts.size(); i++) {
            if (i < classTexts.size() * 0.8) {
                learnWriter.println(classTexts.get(i));
            } else {
                testWriter.println(classTexts.get(i));
            }
        }
        learnWriter.close();
        testWriter.close();
    }

    private static void fillInPositiveAndNegative(Multimap<String, String> labelToTextsMap, String positiveLabel, List<String> positive, List<String> negative) {
        positive.addAll(labelToTextsMap.get(positiveLabel));
        for (Map.Entry<String, Collection<String>> label2Texts : labelToTextsMap.asMap().entrySet()) {
            if (!positiveLabel.equals(label2Texts.getKey())) {
                negative.addAll(label2Texts.getValue());
            }
        }
        Collections.shuffle(positive);
        Collections.shuffle(negative);
    }
}
