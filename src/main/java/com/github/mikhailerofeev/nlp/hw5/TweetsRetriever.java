package com.github.mikhailerofeev.nlp.hw5;

import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author m-erofeev
 * @since 21.06.14
 */
public class TweetsRetriever {

    public static final String OAUTH_CONSUMER_KEY = "mimimi";
    private static final String OAUTH_CONSUMER_SECRET = "lalala";

    public static void main(String[] args) throws IOException, InterruptedException {
//        retrieveN(2000, "%3A)", new File("src/main/resources/hw5/smile-pos.txt"));
//        retrieveN(2000, "%3A(", new File("src/main/resources/hw5/smile-pos.txt"));
        retrieveN(500, "гугл", new File("src/main/resources/hw5/google-smile-pos.txt"));
    }

    private static void retrieveN(int n, String query, File toSave) throws IOException {
        Set<String> positive = Sets.newHashSet();
        Long minId = null;
        while (positive.size() < n) {
            System.out.println(positive.size());
            minId = retrieveTweetsAndMinId(minId, query, positive);
        }
        FileUtils.writeLines(toSave, positive);
    }

    private static long retrieveTweetsAndMinId(Long maxId, String query, Set<String> toRetrive) throws IOException {
        String accessToken = getAccessToken();

        String baseUrl = "https://api.twitter.com/1.1/search/tweets.json";
        String url = baseUrl + "?q=" + query + "&lang=ru&locale=ru&count=100&include_entities=false";
        if (maxId != null) {
            url += "&max_id=" + maxId;
        }
//        System.out.println(url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);
        String resp = IOUtils.toString(response.getEntity().getContent());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode statuses;
        try {
            statuses = mapper.readTree(resp).get("statuses");
            return parseTweetsAndFindMinId(statuses, toRetrive);
        } catch (Exception e) {
            System.err.println(resp);
            throw e;
        }
    }

    public static long parseTweetsAndFindMinId(JsonNode statusesNode, Set<String> toRetrieve) throws IOException {
        long i = Long.MAX_VALUE;
        for (JsonNode status : statusesNode) {
            long id = status.get("id").asLong();
            if (i > id) {
                i = id;
            }
            String text = status.get("text").asText().replaceAll("\n", "");
            if (text.length() > 30) {
                if (text.contains(":)") && text.contains(":(")) {
                    continue;
                }
                toRetrieve.add(text);
            }
        }
        return i;
    }

    private static String getAccessToken() throws IOException {
        String key2Secret = OAUTH_CONSUMER_KEY + ":" + OAUTH_CONSUMER_SECRET;
        String base64ConsumerKeySecret = Base64.encodeBase64String(key2Secret.getBytes());
        String authRequestUrl = "https://api.twitter.com/oauth2/token";
        HttpPost authRequest = new HttpPost(authRequestUrl);
        authRequest.setHeader("Authorization", "Basic " + base64ConsumerKeySecret);
        authRequest.setHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        BasicHttpEntity oauthData = new BasicHttpEntity();
        oauthData.setContent(IOUtils.toInputStream("grant_type=client_credentials"));
        authRequest.setEntity(oauthData);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse oauthResponse = httpClient.execute(authRequest);
        String oauthResponseStr = IOUtils.toString(oauthResponse.getEntity().getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(oauthResponseStr).get("access_token").asText();
        } catch (Exception e) {
            System.err.println(oauthResponseStr);
            throw e;
        }
    }
}
