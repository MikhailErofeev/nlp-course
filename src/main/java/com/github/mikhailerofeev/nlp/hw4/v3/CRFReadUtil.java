package com.github.mikhailerofeev.nlp.hw4.v3;

import com.github.mikhailerofeev.nlp.hw1.StatisticsResult;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author m-erofeev
 * @since 13.07.14
 */
public class CRFReadUtil {

    public static void main(String[] args) throws IOException {
        String outFile = "/Users/m-erofeev/ret.out";
        List<String> out = FileUtils.readLines(new File(outFile));
        Map<String, StatisticsResult.Builder> stats = Maps.newHashMap();
        for (String type : Arrays.asList("I-PER", "B-PER", "I-ORG", "B-ORG", "PER", "O", "ORG")) {
            stats.put(type, new StatisticsResult.Builder());
        }

        for (String token : out) {
            String[] split = token.split("\t");
            if (split.length < 10){
                continue;
            }
            String expected = split[split.length - 2];
            String actual = split[split.length - 1];
            StatisticsResult.Builder expectedTypeStat = stats.get(expected);
            StatisticsResult.Builder actualTypeStat = stats.get(actual);
            if (expected.equals(actual)) {
                expectedTypeStat.addTp(1);
            } else {
                expectedTypeStat.addFn(1);
                actualTypeStat.addFp(1);
            }
        }
        StatisticsResult.Builder all = new StatisticsResult.Builder();
        for (Map.Entry<String, StatisticsResult.Builder> type2stat : stats.entrySet()) {
            StatisticsResult result = type2stat.getValue().build();
            System.out.println(type2stat.getKey() + "\t" + result);
            all.addResult(result);
        }
        System.out.println("all\t" + all.build());
    }
}
