package com.github.mikhailerofeev.nlp.hw5;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 24.06.14
 */
public class BestWordsCollector {

    public static void main(String[] args) throws IOException {
        Set<String> negFilesWthoutProcessing = Sets.newHashSet(
                "src/main/resources/hw5/most-neg-words.txt",
                "../mikhaylova_moiseeva_bakradze/hw5/neg-words",
                "../nlp-3/Homework5/neg.out",
                "../nlp-5/HW-5/negative_words.txt"
        );

        Set<String> posFilesWithoutProcessing = Sets.newHashSet(
                "src/main/resources/hw5/most-pos-words.txt",
                "../mikhaylova_moiseeva_bakradze/hw5/pos-words",
                "../nlp-3/Homework5/pos.out",
                "../nlp-5/HW-5/positive_words.txt");

        Set<String> resultBad = Sets.newTreeSet();
        Set<String> resultGood = Sets.newTreeSet();

        readAndRemoveNumbers(resultBad, "../nlp-1/homeworks/data/SentimentAnalysis/bad_filtered", ",");
        readAndRemoveNumbers(resultGood, "../nlp-1/homeworks/data/SentimentAnalysis/good_filtered", ",");

        readAndRemoveNumbers(resultBad, "../spbsu1/hw5/corpus/words/apple-neg-cleaned.txt", "\t");
        readAndRemoveNumbers(resultGood, "../spbsu1/hw5/corpus/words/apple-pos-cleaned.txt", "\t");

        read(resultBad, negFilesWthoutProcessing);
        read(resultGood, posFilesWithoutProcessing);
        for (String s : resultBad) {
            System.out.println(s);
        }
        System.out.println("----------GOOOOOOOD-------------");
        for (String s : resultGood) {
            System.out.println(s);
        }
    }

    private static void read(Set<String> result, Set<String> filesWthoutProcessing) throws IOException {
        for (String s : filesWthoutProcessing) {
            List<String> srcWords = FileUtils.readLines(new File(s));
            result.addAll(srcWords);
        }
    }

    private static void readAndRemoveNumbers(Set<String> words, String pathname, String split) throws IOException {
        List<String> srcWords = FileUtils.readLines(new File(pathname));
        srcWords = srcWords.subList(1, srcWords.size()); //title
        for (String word : srcWords) {
            words.add(word.split(split)[0]);
        }
    }
}
