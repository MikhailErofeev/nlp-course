package com.github.mikhailerofeev.nlp.hw5;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static junit.framework.Assert.assertFalse;

public class TweetsRetrieverTest {

    @Test
    public void testParseTweets() throws Exception {
        String resp = FileUtils.readFileToString(new File("src/test/resources/tweets.json"));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode statuses = mapper.readTree(resp).get("statuses");
        Set<String> ret = Sets.newHashSet();
        TweetsRetriever.parseTweetsAndFindMinId(statuses, ret);
        assertFalse(ret.isEmpty());
        for (String tweet : ret) {
            System.out.println(tweet);
        }
    }
}