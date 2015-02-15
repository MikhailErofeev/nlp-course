package com.github.mikhailerofeev.nlp.hw3;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 24.06.14
 */
public class ChiSquareTest {

    private final List<String> positiveTexts;
    private final List<String> negativeTexts;
    private final NaiveBayesClassifier classifier;

    public ChiSquareTest(List<String> positiveTexts, List<String> negativeTexts, NaiveBayesClassifier classifier) {
        this.positiveTexts = positiveTexts;
        this.negativeTexts = negativeTexts;
        this.classifier = classifier;
    }

    public void calculate(final int minimalWordOccurence) {
        //find number of expected matches and minus observed real value
        //better solution -- match intersections, not sum
        //http://math.hws.edu/javamath/ryan/ChiSquare.html         
        Predicate<Map.Entry<String, Integer>> occurencePredicate = new Predicate<Map.Entry<String, Integer>>() {
            @Override
            public boolean apply(Map.Entry<String, Integer> stringIntegerEntry) {
                return stringIntegerEntry.getValue() > minimalWordOccurence;
            }
        };
        final Map<String, Integer> expectedPositiveWords = Maps.filterEntries(NaiveBayesClassifierUtils.wordToCount(positiveTexts), occurencePredicate);
        final Map<String, Integer> expectedNegativeWords = Maps.filterEntries(NaiveBayesClassifierUtils.wordToCount(negativeTexts), occurencePredicate);
        final Map<String, Integer> observedPositiveWords = Maps.newHashMap();
        final Map<String, Integer> observedNegativeWords = Maps.newHashMap();

        for (String text : Iterables.concat(positiveTexts, negativeTexts)) {
            List<String> words = NaiveBayesClassifierUtils.splitTextToWordsAndNormalize(text);
            boolean isPositive = classifier.getChanceForText(text) >= 1;
            for (String word : words) {
                Map<String, Integer> observed = isPositive ? observedPositiveWords : observedNegativeWords;
                int count = Optional.fromNullable(observed.get(word)).or(0);
                observed.put(word, count + 1);
            }
        }
        System.out.println("----------pos-----------------------------------------------------------------");
        Set<String> significantPositive = getSignificant(expectedPositiveWords, observedPositiveWords);
        for (String s : significantPositive) {
            System.out.println(s);
        }
        System.out.println("----------neg-----------------------------------------------------------------");
        Set<String> significantNegative = getSignificant(expectedNegativeWords, observedNegativeWords);
        for (String s : significantNegative) {
            System.out.println(s);
        }
    }

    private Set<String> getSignificant(Map<String, Integer> expectedWords, Map<String, Integer> observedWords) {
        Set<String> okWords = Sets.newTreeSet();
        for (Map.Entry<String, Integer> word2ExpectedCount : expectedWords.entrySet()) {
            String word = word2ExpectedCount.getKey();
            long expected = word2ExpectedCount.getValue().longValue();
            long observed = Optional.fromNullable(observedWords.get(word)).or(0).longValue();
            double chiSquare = Math.pow(expected - observed, 2) / expected;
            if (chiSquare <= 0.15) { //p-value >= 0.7
//                System.out.println(okWords.size() + "\t" + word + "\t" + expected + "\t" + observed + "\t" + chiSquare);
//                System.out.println(word);
                okWords.add(word);
            }

        }
        return okWords;
    }
}
