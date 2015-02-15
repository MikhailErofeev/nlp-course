package com.github.mikhailerofeev.nlp.hw4;

import com.github.mikhailerofeev.nlp.hw1.StatisticsResult;
import com.github.mikhailerofeev.nlp.hw4.v1.CorpusReader;
import com.github.mikhailerofeev.nlp.hw4.v1.Fact;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class CorpusReaderTest {

    Multimap<String, Fact> id2Facts;

    @Before
    public void setUp() throws Exception {
        id2Facts = CorpusReader.parseRomipFacts();

    }

    @Test
    @Ignore
    public void testRetrieveFactsFromSrc() throws Exception {
        final Map<String, String> id2News = CorpusReader.parseNews();

        final String key = "23158";
        final String newsText = id2News.get(key);
        final Collection<Fact> sets = id2Facts.get(key);
        assertFalse(id2Facts.isEmpty());
        assertFalse(sets.isEmpty());
        assertNotNull(newsText);
    }

    @Test
    public void testFindFacts() throws Exception {
        final String key = "23158";
        String txt = FileUtils.readFileToString(new File("src/main/resources/hw4/" + key + ".news"));
        Map<String, Fact> myFacts = CorpusReader.findMyFacts(txt);
        Map<String, Fact> expectedFacts = CorpusReader.getMyFactIdToFact(id2Facts.get(key));
        StatisticsResult stat = CorpusReader.getFactsQuality(expectedFacts, myFacts);
        assertEquals(0.45, stat.getAccuracy());
        System.out.println(stat);
    }

    @Test
    public void testFindFacts2() throws Exception {
        final String key = "11999";
        String txt = FileUtils.readFileToString(new File("src/main/resources/hw4/" + key + ".news"));
        Map<String, Fact> myFacts = CorpusReader.findMyFacts(txt);
        Map<String, Fact> expectedFacts = CorpusReader.getMyFactIdToFact(id2Facts.get(key));
        StatisticsResult stat = CorpusReader.getFactsQuality(expectedFacts, myFacts);
        System.out.println(stat);
        assertEquals(0.45, stat.getAccuracy());
    }
}