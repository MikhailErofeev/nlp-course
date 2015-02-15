package com.github.mikhailerofeev.nlp.hw2;

import com.google.common.collect.Multimap;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 17.05.14
 */
public class TrainingDataFormatter {

    public static void main(String[] args) throws Exception {
        new TrainingDataFormatter().generateFiles();
    }


    private void generateFiles() throws Exception {
        boolean onlyMyTexts = false;
//                final Set<String> niceWordsOrNull = null;
        final Set<String> niceWordsOrNull = DataNormalizers.getNiceWords();
        final Map<String, Set<String>> labels2Files = TextsFinder.fillInFileNames(onlyMyTexts);
        Multimap<String, String> labels2Texts = DataNormalizers.createLabelToTextsMap(labels2Files, niceWordsOrNull);
//        Multimap<String, String> labels2Texts = DataNormalizers.createLabelToSentenceMap(labels2Files, niceWordsOrNull);

        File csvFile = createAndSaveCsvFile(labels2Texts);
//        csvAndFiltering(csvFile);
    }

    private void csvAndFiltering(File csvFile) throws Exception {
        //tries to automate
        final Instances dataSet = getInstancesFromCsv(csvFile);
        final StringToWordVector stringToWordVector = new StringToWordVector(100000);
        stringToWordVector.setAttributeIndices("last");
        stringToWordVector.setInputFormat(dataSet);
        stringToWordVector.setLowerCaseTokens(true);
        final Reorder reorder = new Reorder();
        Instances vectorizedDataSet = Filter.useFilter(dataSet, stringToWordVector);
        reorder.setAttributeIndices("2-last,1");
        reorder.setInputFormat(vectorizedDataSet);
        vectorizedDataSet = Filter.useFilter(vectorizedDataSet, reorder);
//        for (Instance instance : vectorizedDataSet) {
//            System.out.println(instance);
//        }
        System.out.println(vectorizedDataSet.get(0));
    }

    private Instances getInstancesFromCsv(File file) throws IOException {
        final CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(file);
        csvLoader.setFieldSeparator(",");
        csvLoader.setEnclosureCharacters("'");
        csvLoader.setNominalAttributes("first");
        csvLoader.setStringAttributes("last");
//        csvLoader.setNoHeaderRowPresent(true);
        return csvLoader.getDataSet();
    }

//    society, politics, auto, accidents, culture, sport, hi-tech, science, economics, internet


    private File createAndSaveCsvFile(Multimap<String, String> labels2Sentences) throws FileNotFoundException {
        File file = new File("label-sentence.csv");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = new PrintStream((file));
        out.println("label,sentence;");
        for (Map.Entry<String, Collection<String>> label2Sentences : labels2Sentences.asMap().entrySet()) {
            for (String sentence : label2Sentences.getValue()) {
                final String replaced = sentence.replaceAll("[;,']", " ");
                out.println(label2Sentences.getKey() + "," + replaced + "");
            }
        }
        out.close();
        return file;
    }
}
