package com.xtc.moment.util;

import com.xtc.moment.db.bean.DbMoment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MomentSearchTextUtilTest {

    @Test
    public void normalize_trimsAndUsesLocaleRootLowercase() {
        assertEquals("hello 好友圈", MomentSearchTextUtil.normalize("  HeLLo 好友圈  "));
        assertEquals("", MomentSearchTextUtil.normalize(null));
        assertEquals("", MomentSearchTextUtil.normalize(""));
    }

    @Test
    public void keywordValidationEnforcesLengthLimit() {
        assertFalse(MomentSearchTextUtil.isValidKeyword(null));
        assertFalse(MomentSearchTextUtil.isValidKeyword("   "));
        assertTrue(MomentSearchTextUtil.isValidKeyword("a"));
        assertTrue(MomentSearchTextUtil.isValidKeyword(repeat('a', 30)));
        assertFalse(MomentSearchTextUtil.isValidKeyword(repeat('a', 31)));
    }

    @Test
    public void matchesPlainFieldsCaseInsensitively() {
        DbMoment moment = new DbMoment();
        moment.setContent("Weekend at the PARK");
        moment.setDescription("Sunny day");
        moment.setLocation("Shanghai");
        moment.setName("Alice");

        String searchable = MomentSearchTextUtil.extractSearchableText(moment);

        assertTrue(searchable.contains("weekend at the park"));
        assertTrue(MomentSearchTextUtil.matches(moment,
                MomentSearchTextUtil.normalize("park")));
        assertTrue(MomentSearchTextUtil.matches(moment,
                MomentSearchTextUtil.normalize("ALICE")));
        assertFalse(MomentSearchTextUtil.matches(moment,
                MomentSearchTextUtil.normalize("rain")));
    }

    @Test
    public void jsonValuesAreFlattenedWithoutExposingMetadataKeys() {
        DbMoment moment = new DbMoment();
        moment.setContent("{\"content\":\"公开动态\",\"resource\":\"https://example.test/a.jpg\","
                + "\"location\":\"上海\",\"title\":\"旅行\",\"ignored\":{\"name\":\"隐藏\"}}");

        String searchable = MomentSearchTextUtil.extractSearchableText(moment);

        assertTrue(searchable.contains("公开动态"));
        assertTrue(searchable.contains("上海"));
        assertTrue(searchable.contains("旅行"));
        assertFalse(searchable.contains("example.test"));
        assertFalse(searchable.contains("a.jpg"));
        assertFalse(searchable.contains("隐藏"));
    }

    @Test
    public void jsonArraysAndNestedWhitelistedValuesAreSearchable() {
        DbMoment moment = new DbMoment();
        moment.setPublishContent("{\"content\":[\"first\",\"second\"],"
                + "\"description\":{\"textMsg\":\"nested text\"}}");

        String searchable = MomentSearchTextUtil.extractSearchableText(moment);

        assertTrue(searchable.contains("first"));
        assertTrue(searchable.contains("second"));
        assertTrue(searchable.contains("nested text"));
    }

    @Test
    public void malformedJsonFallsBackToPlainTextSearch() {
        DbMoment moment = new DbMoment();
        moment.setContent("{not valid json, but still my words");

        String searchable = MomentSearchTextUtil.extractSearchableText(moment);

        assertTrue(searchable.contains("{not valid json, but still my words"));
        assertTrue(MomentSearchTextUtil.matches(moment, "my words"));
    }

    @Test
    public void nullMomentAndEmptyKeywordDoNotMatch() {
        DbMoment moment = new DbMoment();
        moment.setContent("text");

        assertFalse(MomentSearchTextUtil.matches(null, "text"));
        assertFalse(MomentSearchTextUtil.matches(moment, ""));
        assertEquals("", MomentSearchTextUtil.extractSearchableText(null));
    }

    private static String repeat(char value, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}
