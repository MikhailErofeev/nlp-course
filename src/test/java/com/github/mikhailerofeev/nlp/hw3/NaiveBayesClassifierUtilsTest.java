package com.github.mikhailerofeev.nlp.hw3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class NaiveBayesClassifierUtilsTest {

    @Test
    public void testBuild() throws IOException {
        String[] positive = new String[]{"футбольная команда россии купила билеты на чемпионат мира",
                "футбол в россии это весело", "ура футбол хоккей вперёд"};
        String[] negative = new String[]{"россия купила билет в крым",
                "команда сделала бизнес-предложение",
                "у меня есть билет в кино.",
                "россия чемпион по случайному слову"};
        final NaiveBayesClassifier classifier = NaiveBayesClassifierUtils.buildClassifier(positive, negative, 0);
        assertEquals(3. / 7, classifier.getClassChance());
        classifier.getClassChance();
        System.out.println(classifier.getWord2classChance());
        assertEquals(0.5, classifier.getWord2classChance().get("росс"));
        assertEquals(1.0, classifier.getWord2classChance().get("чемпионат"));
        assertEquals(1.0, classifier.getWord2classChance().get("футбол"));
        assertEquals(0.0, classifier.getWord2classChance().get("крым"));
        assertEquals(0.5, classifier.getWord2classChance().get("купил"));
        assertEquals(1.0, classifier.getChanceForText("ура футбол хоккей вперёд"));
        assertEquals(1.0, classifier.getChanceForText("купил билеты на хоккей"));
        assertEquals(0.37499999999999994, classifier.getChanceForText("россия купил билет"));
        assertEquals(1.0, classifier.getChanceForText("россия купил футбол"));
    }
}