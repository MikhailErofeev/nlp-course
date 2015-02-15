package com.github.mikhailerofeev.nlp.hw4.v1;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author m-erofeev
 * @since 18.05.14
 */
public class FactsRetriever {

    public static final int MAX_ORGANIZATION_LENGTH = 44;
    private static Pattern ORG_FULL_PATTERN;
    private static Pattern ROLE_ORG_I_O_PATTERN;
    private static Pattern ABBR_PATTERN;
    private static Pattern START_WITH_ABBR_PATTERN;
    private static Pattern NAME_PATTERN;
    public static String ROLE_ORG_IO_REGEX;

    static {
        final String role = "([А-Я]?[а-я]{3,})( [а-я]{1,2})?"; //@FIXME костыльно-ориентированное программирование

        String organizationStartName = "[A-ZА-Я]+([a-zа-я]){0,}";
        String manyWordsOrganizationName = "(( [A-Za-za-я]+){0,}( [a-za-я]{3,}){1,})?";
        String abbr = "( ?\\(?[А-ЯA-Z]{2,}\\)?)";

        ORG_FULL_PATTERN = Pattern.compile(organizationStartName + manyWordsOrganizationName);

        final String organization = "\"?" + organizationStartName + manyWordsOrganizationName + abbr + "?\"?";
        ABBR_PATTERN = Pattern.compile(abbr);
        START_WITH_ABBR_PATTERN = Pattern.compile("^" + abbr);
        final String names = "(([А-Я][а-я]+)|[А-Я]\\.) ?(([А-Я][а-я]+)(-([А-Я][а-я]+))?|([A-Я]){3,})";
        ROLE_ORG_IO_REGEX = role + " " + organization + " " + names;
        ROLE_ORG_I_O_PATTERN = Pattern.compile(ROLE_ORG_IO_REGEX);
        NAME_PATTERN = Pattern.compile(names);
    }

    private Map<String, String> abbr2FullName = Maps.newHashMap();

    public FactsRetriever() {

    }

    public static List<Fact> retrieveStatic(String text) {
        return new FactsRetriever().retrieve(text);
    }

    public List<Fact> retrieve(String sentence) {
        final Matcher roleOrgIOPattern = ROLE_ORG_I_O_PATTERN.matcher(sentence);
        List<Fact> ret = Lists.newArrayList();
        while (roleOrgIOPattern.find()) {
            String match = roleOrgIOPattern.group();
            final int start = roleOrgIOPattern.start();
            final int end = roleOrgIOPattern.end();

            //remove name from match and split
            List<String> words = Lists.newArrayList(match.split(" "));
            String lastWord = words.get(words.size() - 1);
            final String lastName;
            final String firstName;
            if (NAME_PATTERN.matcher(lastWord).find() && lastWord.split("\\.").length == 2) {
                String[] name2last = lastWord.split("\\.");
                lastName = name2last[1];
                firstName = name2last[0] + ".";
            } else {
                lastName = words.remove(words.size() - 1);
                firstName = words.remove(words.size() - 1);
            }
            match = match.replaceAll(lastName, "");
            match = match.replaceAll(firstName, "");

            //remove role from match and split
            if (!isOrganizationWord(words.get(0))) {
                String role = words.remove(0);
                match = match.replaceAll(role, "");
            }

            String fullOrganizationNameWithOptionalAbbr = trimSpaceAndScopes(match);
            Matcher nameMatcher = NAME_PATTERN.matcher(fullOrganizationNameWithOptionalAbbr);
//            if (matcher.find()) {
//                continue;
//            }
            if (nameMatcher.find()) {
                //@FIXME больше костылей для бога костылей!
                if (fullOrganizationNameWithOptionalAbbr.length() > 30) { //it's not just name, but something interesting
                    fullOrganizationNameWithOptionalAbbr = fullOrganizationNameWithOptionalAbbr.substring(nameMatcher.end());
                } else {
                    continue;
                }
            }
            final String organization = retrieveOrganizationFullNameAndSaveAbbr(fullOrganizationNameWithOptionalAbbr);
            if (organization.length() > MAX_ORGANIZATION_LENGTH) {
                continue;
            }
            final Fact fact = new Fact(organization, lastName + " " + firstName, end - start, end - start, -1, "employee", -1);
            ret.add(fact);
        }

        return ret;
    }

    private String retrieveOrganizationFullNameAndSaveAbbr(String fullOrganizationNameWithOptionalAbbr) {
        fullOrganizationNameWithOptionalAbbr = trimSpaceAndScopes(fullOrganizationNameWithOptionalAbbr);
        fullOrganizationNameWithOptionalAbbr = tryToRemoveRoles(fullOrganizationNameWithOptionalAbbr); //trash data
        Matcher onlyAbbrMatcher = START_WITH_ABBR_PATTERN.matcher(fullOrganizationNameWithOptionalAbbr);
        //Председателем правления BITE стал депутат Яков Плинер. remove trash "стал депутат"
        if (onlyAbbrMatcher.find()) {
            String abbr = trimSpaceAndScopes(onlyAbbrMatcher.group());
            return Optional.fromNullable(abbr2FullName.get(abbr)).or(abbr);
        }
        Matcher abbrMatcher = ABBR_PATTERN.matcher(fullOrganizationNameWithOptionalAbbr);
        if (abbrMatcher.find()) {
            String abbr = abbrMatcher.group();
            abbr = trimSpaceAndScopes(abbr);
            Matcher onlyFullOrganization = ORG_FULL_PATTERN.matcher(fullOrganizationNameWithOptionalAbbr);
            onlyFullOrganization.find();
            String fullOrgValue = onlyFullOrganization.group();
            abbr2FullName.put(abbr, fullOrgValue);
            return fullOrgValue;
        } else {
            return fullOrganizationNameWithOptionalAbbr;
        }
    }

    private String tryToRemoveRoles(String fullOrganizationNameWithOptionalAbbr) {
        List<String> ret = Lists.newArrayList();
        Set<String> roles = Sets.newHashSet("зам", "начальник", "завед");
        sourceWordFor:
        for (String s : fullOrganizationNameWithOptionalAbbr.split(" ")) {
            for (String role : roles) {
                if (s.toUpperCase().contains(role.toUpperCase())) {
                    continue sourceWordFor;
                }
            }
            ret.add(s);
        }
        return Joiner.on(" ").join(ret);
    }


    private boolean isOrganizationWord(String s) {
        Set<String> organizations = Sets.newHashSet("администрац");
        for (String organization : organizations) {
            if (s.toUpperCase().contains(organization.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private String trimSpaceAndScopes(String abbr) {
        abbr = abbr.trim();
        abbr = StringUtils.strip(abbr, ")");
        abbr = StringUtils.strip(abbr, "(");
        return abbr;
    }

    public Map<String, String> getAbbr2FullName() {
        return abbr2FullName;
    }
}
