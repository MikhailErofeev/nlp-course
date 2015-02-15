package com.github.mikhailerofeev.nlp.hw4.v2;

import com.github.mikhailerofeev.nlp.hw4.v1.CorpusReader;
import com.github.mikhailerofeev.nlp.hw4.v1.Fact;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author m-erofeev
 * @since 26.06.14
 */
public class CorpusFactsConverter {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Multimap<String, Fact> files2Facts = CorpusReader.parseRomipFacts();
        for (Fact fact : files2Facts.values()) {
            for (String s : fact.getFirstText().split(" ")) {
                if (s.length() < 2) {
                    continue;
                }
                s = StringUtils.strip(s, "\"");
                if (StringUtils.isAllUpperCase(s) && s.length() > 3) {
                    s = s.charAt(0) + s.substring(1).toLowerCase();
                }
                System.out.println(s + "\tORG");
            }
            for (String s : fact.getSecondText().split(" ")) {
                if (s.length() < 2) {
                    continue;
                }
                if (StringUtils.isAllUpperCase(s)) {
                    s = s.charAt(0) + s.substring(1).toLowerCase();
                }
                System.out.println(s + "\tPER");
            }

        }


    }

}
