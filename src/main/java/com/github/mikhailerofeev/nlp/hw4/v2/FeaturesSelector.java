package com.github.mikhailerofeev.nlp.hw4.v2;

import com.github.mikhailerofeev.nlp.hw2.DataProcessingUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author m-erofeev
 * @since 26.06.14
 */
public class FeaturesSelector {

    private static final String DEV_DATA = "/Users/m-erofeev/projects/nlp/russian-ner/ru_corpus.dev.txt";
    private static final String TRAIN_DATA = "/Users/m-erofeev/projects/nlp/russian-ner/ru_corpus.train.txt";
    private static final String TEST_DATA = "/Users/m-erofeev/projects/nlp/russian-ner/ru_corpus.test.txt";


    private static final String ROMIP_SIMPLE_FACTS = "src/main/resources/hw4/romip-facts.txt";


    public static void main(String[] args) throws IOException {
        List<String> entries = FileUtils.readLines(new File(TEST_DATA));
        entries = simpifyToPerOrOrg(entries);
//        entries.addAll(FileUtils.readLines(new File(ROMIP_SIMPLE_FACTS)));
        new FeaturesSelector().generateFeaturesAndPrint(entries);
    }

    private static List<String> simpifyToPerOrOrg(List<String> entries) {
        entries = Lists.newArrayList(Collections2.transform(entries, new Function<String, String>() {
            @Override
            public String apply(String s) {
                s = s.replace("I-PER", "PER");
                s = s.replace("I-ORG", "ORG");
                s = s.replace("B-PER", "PER");
                s = s.replace("B-ORG", "ORG");
                return s;
            }
        }));
        return entries;
    }

    private Predicate<String> latin = new Predicate<String>() {

        Pattern latin = Pattern.compile("^[a-zA-Z]+$");

        @Override
        public boolean apply(String s) {
            return latin.matcher(s).find();
        }
    };

    private Predicate<String> russian = new Predicate<String>() {

        Pattern russian = Pattern.compile("^[а-яА-Я]+$");

        @Override
        public boolean apply(String s) {
            return russian.matcher(s).find();
        }
    };


    private Predicate<String> allUpperCase = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return StringUtils.isAllUpperCase(s);
        }
    };

    private Predicate<String> startUpperCaseAndAllLowerCase = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return Character.isUpperCase(s.charAt(0)) && (s.length() == 1 || StringUtils.isAllLowerCase(s.substring(1)));
        }
    };

    private Predicate<String> startUpperCaseAndHasUpperCases = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return Character.isUpperCase(s.charAt(0))
                    && s.length() > 1 && !StringUtils.isAllLowerCase(s.substring(1)) && !StringUtils.isAllUpperCase(s.substring(1));
        }
    };

    private Predicate<String> adjective = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return DataProcessingUtils.isAdjective(s);
        }
    };

    private Predicate<String> shortName = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return s.length() <= 3;
        }
    };

    private Predicate<String> mediumName = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return s.length() > 3 && s.length() <= 7;
        }
    };

    private Predicate<String> longName = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return s.length() > 7;
        }
    };

    private Predicate<String> personEnd = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return DataProcessingUtils.isPeople(s);
        }
    };


    private Predicate<String> IPer = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return russian.apply(s) && (mediumName.apply(s) || longName.apply(s))
                    && personEnd.apply(s) && startUpperCaseAndAllLowerCase.apply(s);
        }
    };

    private Predicate<String> IOrg = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            boolean shortLatin = latin.apply(s) && startUpperCaseAndAllLowerCase.apply(s) && (shortName.apply(s) || mediumName.apply(s));
            boolean russianAdj = russian.apply(s) && StringUtils.isAllLowerCase(s) && adjective.apply(s);
            return shortLatin || russianAdj;
        }
    };

    private Predicate<String> BOrg = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return allUpperCase.apply(s) || startUpperCaseAndHasUpperCases.apply(s);
        }
    };

    private void generateFeaturesAndPrint(List<String> entries) throws IOException {
        List<Predicate<String>> predicates = Lists.newArrayList(
                latin, russian,
                allUpperCase, startUpperCaseAndAllLowerCase, startUpperCaseAndHasUpperCases,
                adjective, IPer, IOrg, BOrg,
                shortName, mediumName, longName
        );
        String s = "type,latin,russian," +
                "allUpperCase,startUpperCaseAndAllLowerCase,startUpperCaseAndHasUpperCases," +
                "adjective,IPer,IOrg,BOrg," +
                "shortName,mediumName,longName;";
        System.out.println(s);
        for (String devEntity : entries) {
            String[] split = devEntity.split("\t");
            if (split.length != 2) {
                continue;
            }
            final String word = split[0];
            final String type = split[1];

            Collection<Boolean> features = Collections2.transform(predicates, new Function<Predicate<String>, Boolean>() {
                @Override
                public Boolean apply(Predicate<String> stringPredicate) {
                    return stringPredicate.apply(word);
                }
            });
//            System.out.println(adjective.apply(word) + "\t" + word);
//            System.out.println(IPer.apply(word) + "\t" + word + "\t" + type);
//            if (type.contains("PER")) {
//            System.out.println(buildString(word, type, features, "\t"));
            String retString = buildString(word, type, features, " ");
//            retString = retString.substring(retString.indexOf(',') + 1);
            System.out.println(retString);
//            }
        }
    }

    private String buildString(String word, String type, Collection<Boolean> features, String delim) {
        StringBuilder builder = new StringBuilder(word);
        for (Boolean aBoolean : features) {
            builder.append(delim + (aBoolean ? "T" : "F"));
        }
        builder.append(delim + type);
//        builder.append("\n");
        return builder.toString();
    }
}
