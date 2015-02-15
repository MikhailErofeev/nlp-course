package com.github.mikhailerofeev.nlp.hw4.v1;

import com.github.mikhailerofeev.nlp.hw1.SentenceParser;
import com.github.mikhailerofeev.nlp.hw1.StatisticsResult;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class CorpusReader {

    public static final String ROMIP_2005_FACTS_FOLDER = "/Users/m-erofeev/projects/nlp/romip2005-facts/";

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        final Map<String, String> ids2News = parseNews();
        final Multimap<String, Fact> ids2Facts = parseRomipFacts();
        StatisticsResult.Builder builder = new StatisticsResult.Builder();
        for (Map.Entry<String, String> id2news : ids2News.entrySet()) {
            String id = id2news.getKey();
            Collection<Fact> expectedFacts = ids2Facts.get(id);
            if (expectedFacts != null) {
                Map<String, Fact> myFacts = findMyFacts(id2news.getValue());
                StatisticsResult factsQuality = getFactsQuality(myFacts, getMyFactIdToFact(expectedFacts));
                if ("11999".equals(id) || "47496".equals(id) || "62054".equals(id)) {
                    System.out.println(id + "\t" + factsQuality);
                    System.out.println(id2news.getValue());
                }
                builder.addResult(factsQuality);
            }
        }
        System.out.println(builder.build());
    }

    public static Multimap<String, Fact> parseRomipFacts() throws ParserConfigurationException, IOException, SAXException {
        final Multimap<String, Fact> ret = HashMultimap.create();
        String factsFile = ROMIP_2005_FACTS_FOLDER + "and_good_table.xml";
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final InputStream inputStream = IOUtils.toInputStream(FileUtils.readFileToString(new File(factsFile), "windows-1251"), "windows-1251");
        Document doc = dBuilder.parse(inputStream);
        final Element root = doc.getDocumentElement();
        root.normalize();
        final NodeList tasks = root.getChildNodes();
        for (int i = 0; i < tasks.getLength(); i++) {
            final Node task = tasks.item(i);
            if ("task".equals(task.getNodeName())) {
                final String id = task.getAttributes().getNamedItem("id").getTextContent().split("-")[1];
                Set<Fact> factSet = getFacts(task);
                ret.putAll(id, factSet);
            }

        }
        return ret;
    }

    private static Set<Fact> getFacts(Node task) {
        Set<Fact> factSet = Sets.newHashSet();
        for (int j = 0; j < task.getChildNodes().getLength(); j++) {
            final Node factNode = task.getChildNodes().item(j);
            if (!"fact".equals(factNode.getNodeName())) {
                continue;
            }
            final NamedNodeMap attributes = factNode.getAttributes();
            final String firstText = attributes.getNamedItem("firstText").getTextContent();
            final String secondText = attributes.getNamedItem("secondText").getTextContent();
            final String category = attributes.getNamedItem("cathegory").getTextContent();
            final int systemLength = Integer.parseInt(attributes.getNamedItem("systemLength").getTextContent());
            final int systemOffset = Integer.parseInt(attributes.getNamedItem("systemOffset").getTextContent());
            final int userLength = Integer.parseInt(attributes.getNamedItem("userLength").getTextContent());
            final int entityOffset = Integer.parseInt(attributes.getNamedItem("entityOffset").getTextContent());
            factSet.add(new Fact(firstText, secondText, systemLength, userLength, systemOffset, category, entityOffset));
        }
        return factSet;
    }

    public static Map<String, String> parseNews() throws ParserConfigurationException, SAXException, IOException {
        String newsShevardFileName = "news-shevard.xml";
        String newsFile = ROMIP_2005_FACTS_FOLDER + newsShevardFileName;
        Map<String, String> ret = Maps.newHashMap();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final InputStream inputStream = IOUtils.toInputStream(FileUtils.readFileToString(new File(newsFile), "windows-1251"), "windows-1251");
        Document doc = dBuilder.parse(inputStream);
        final Element root = doc.getDocumentElement();
        final NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node document = childNodes.item(i);
            if ("document".equals(document.getNodeName())) {
                Pair<String, String> id2Text = findIDAndText(document);
                ret.put(id2Text.getLeft(), id2Text.getRight());
            }
        }
        return ret;
    }

    private static Pair<String, String> findIDAndText(Node document) {
        String id = null;
        String content = null;
        final NodeList childNodes = document.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            if ("docID".equals(item.getNodeName())) {
                id = item.getTextContent().split("-")[1];
            } else if ("content".equals(item.getNodeName())) {
                content = new String(Base64.decodeBase64(item.getTextContent()), Charset.forName("windows-1251"));
            }
        }
        return Pair.of(id, content);
    }

    public static Map<String, Fact> findMyFacts(String txt) {
        Map<String, Fact> myFacts = Maps.newHashMap();
        FactsRetriever retriever = new FactsRetriever();
        for (String sentence : SentenceParser.parseText(txt).getSentences()) {
            List<Fact> retrieveFacts = retriever.retrieve(sentence);
            for (Fact retrieveFact : retrieveFacts) {
                myFacts.put(retrieveFact.getMyFactCode(), retrieveFact);
            }
        }
        return myFacts;
    }


    public static Map<String, Fact> getMyFactIdToFact(Collection<Fact> facts) {
        Map<String, Fact> expectedFacts = Maps.newHashMap();
        for (Fact fact : facts) {
            expectedFacts.put(fact.getMyFactCode(), fact);
        }
        return expectedFacts;
    }

    public static StatisticsResult getFactsQuality(Map<String, Fact> expectedFacts, Map<String, Fact> myFacts) {
        StatisticsResult.Builder statBuilder = new StatisticsResult.Builder();
        for (Map.Entry<String, Fact> code2fact : myFacts.entrySet()) {
            Fact fact = expectedFacts.get(code2fact.getKey());
            if (code2fact.getValue().weakEquals(fact)) {
//                System.out.println("NICE!\t" + code2fact.getValue() + "\n" + fact + "\n");
                statBuilder.addTp(1);
            } else {
                statBuilder.addFp(1);
            }
        }
        for (Map.Entry<String, Fact> code2fact : expectedFacts.entrySet()) {
            Fact fact = myFacts.get(code2fact.getKey());
            if (!code2fact.getValue().weakEquals(fact)) {
//                System.out.println(code2fact.getValue() + "\n" + fact + "\n");
                statBuilder.addFn(1);
            }
        }
        return statBuilder.build();
    }
}
