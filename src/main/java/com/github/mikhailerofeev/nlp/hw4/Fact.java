package com.github.mikhailerofeev.nlp.hw4;

import com.github.mikhailerofeev.nlp.hw2.DataNormalizers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class Fact {
    String firstText;
    String secondText;
    int systemLength;
    int userLength;
    int systemOffset;
    String category;
    int entityOffset;

    public Fact(String firstText, String secondText, int systemLength, int userLength, int systemOffset, String category, int entityOffset) {
        this.firstText = sortAndNormalize(firstText);
        this.secondText = sortAndNormalize(secondText);
        this.systemLength = systemLength;
        this.systemOffset = systemOffset;
        this.userLength = userLength;
        this.category = category;
        this.entityOffset = entityOffset;
    }

    private String sortAndNormalize(String еуче) {
        final List<String> splitted = Lists.newArrayList(еуче.split(" "));
        final List<String> splittedNormalized = Lists.newArrayList();
        for (String s : splitted) {
            splittedNormalized.add(DataNormalizers.normalizeWord(s, 0));
        }
        Collections.sort(splittedNormalized);
        return Joiner.on(" ").join(splittedNormalized);
    }

    public String getFirstText() {
        return firstText;
    }

    public String getSecondText() {
        return secondText;
    }

    public int getSystemLength() {
        return systemLength;
    }

    public int getUserLength() {
        return userLength;
    }

    public String getCategory() {
        return category;
    }

    public int getEntityOffset() {
        return entityOffset;
    }

    public int getSystemOffset() {
        return systemOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fact)) return false;

        Fact fact = (Fact) o;

        if (entityOffset != fact.entityOffset) return false;
        if (systemLength != fact.systemLength) return false;
        if (userLength != fact.userLength) return false;
        if (!category.equals(fact.category)) return false;
        if (!firstText.equals(fact.firstText)) return false;
        if (!secondText.equals(fact.secondText)) return false;
        if (systemOffset != fact.systemOffset) return false;

        return true;
    }


    public boolean weakEquals(Fact fact) {
        return fact != null &&
                firstText.equalsIgnoreCase(fact.firstText) &&
                secondText.equalsIgnoreCase(fact.secondText);
    }

    @Override
    public int hashCode() {
        int result = firstText.hashCode();
        result = 31 * result + secondText.hashCode();
        result = 31 * result + systemLength;
        result = 31 * result + userLength;
        result = 31 * result + category.hashCode();
        result = 31 * result + entityOffset;
        result = 31 * result + systemOffset;
        return result;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "firstText='" + firstText + '\'' +
                ", secondText='" + secondText + '\'' +
                ", systemLength=" + systemLength +
                ", userLength=" + userLength +
                ", systemOffset=" + systemOffset +
                ", category='" + category + '\'' +
                ", entityOffset=" + entityOffset +
                '}';
    }

    public String getMyFactCode() {
        return getFirstText() + " " + getSecondText();
    }
}
