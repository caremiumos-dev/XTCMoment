package com.xtc.utils.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void getBlankCreatesRequestedWidth() {
        assertEquals("   ", StringUtil.getBlank(3));
        assertEquals("", StringUtil.getBlank(0));
        assertEquals("", StringUtil.getBlank(-1));
    }

    @Test
    public void ipAndDigitValidationHandlesCommonInputs() {
        assertTrue(StringUtil.isIp("127.0.0.1"));
        assertTrue(StringUtil.isIp("255.255.255.255"));
        assertFalse(StringUtil.isIp("256.0.0.1"));
        assertFalse(StringUtil.isIp("1.2.x.4"));
        assertFalse(StringUtil.isIp(null));

        assertTrue(StringUtil.isDigits("0123456789"));
        assertFalse(StringUtil.isDigits("12a"));
        assertFalse(StringUtil.isDigits(""));
        assertTrue(StringUtil.isStartWithDigit("7abc"));
        assertFalse(StringUtil.isStartWithDigit("abc"));
        assertTrue(StringUtil.isDigit('9'));
        assertFalse(StringUtil.isDigit('x'));
    }

    @Test
    public void urlValidationAcceptsSupportedSchemesOnly() {
        assertTrue(StringUtil.isUrl("https://example.com/a/b?q=1"));
        assertTrue(StringUtil.isUrl("http://127.0.0.1:8000/path"));
        assertTrue(StringUtil.isUrl("ftp://example.com/file.txt"));
        assertFalse(StringUtil.isUrl("file:///tmp/test"));
        assertFalse(StringUtil.isUrl("example.com"));
        assertFalse(StringUtil.isUrl(null));
    }

    @Test
    public void unicodeHelpersHandleAsciiAndChinese() {
        assertEquals("65", StringUtil.stringToUnicode("A"));
        assertEquals("20013", StringUtil.stringToUnicode("中"));
        assertEquals("A", StringUtil.unicodeToString("00065"));
        assertEquals("中", StringUtil.unicodeToString("20013"));
        assertNull(StringUtil.stringToUnicode(null));
        assertNull(StringUtil.unicodeToString(""));
    }

    @Test
    public void fullWidthCharactersAreNormalized() {
        assertEquals("ABC 123", StringUtil.toHalfWidth("ＡＢＣ　１２３"));
        assertEquals("plain", StringUtil.toHalfWidth("plain"));
    }

    @Test
    public void filtersKeepSupportedCharacters() {
        assertEquals("abc123你好", StringUtil.filterChars("abc123你好\uD83D\uDE00"));
        assertEquals("hello [world]! :", StringUtil.filterBrackets("『hello』 【world】！ ： "));
    }

    @Test
    public void chineseDetectionAndWidthAreConsistent() {
        assertTrue(StringUtil.isChineseChar('中'));
        assertFalse(StringUtil.isChineseChar('A'));
        assertTrue(StringUtil.containsChinese("watch 手表"));
        assertFalse(StringUtil.containsChinese("watch"));
        assertEquals(0, StringUtil.getWidth(null));
        assertEquals(5, StringUtil.getWidth("watch"));
        assertEquals(4, StringUtil.getWidth("手表"));
        assertTrue(StringUtil.isAscii('a'));
        assertFalse(StringUtil.isAscii('中'));
    }

    @Test
    public void limitWidthTruncatesByDisplayColumns() {
        assertEquals("abcdefgh", StringUtil.limitWidth("abcdefghi"));
        assertEquals("客户服务", StringUtil.limitWidth("客户服务端"));
        assertEquals("", StringUtil.limitWidth(null));
    }

    @Test
    public void identifierAndCollectionHelpersWork() {
        assertTrue(StringUtil.isChineseId("好友_123abc"));
        assertFalse(StringUtil.isChineseId("好友-123"));
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(Collections.<String>emptyList()));
        assertFalse(StringUtil.isEmpty(Arrays.asList("one")));
    }

    @Test
    public void randomStringUsesRequestedLengthAndAlphabet() {
        String value = StringUtil.randomString(64);

        assertEquals(64, value.length());
        assertTrue(value.matches("[0-9a-z]{64}"));
        assertEquals("", StringUtil.randomString(0));
    }

    @Test
    public void formatTwoDigitsHandlesRanges() {
        assertEquals("00", StringUtil.formatTwoDigits(0));
        assertEquals("07", StringUtil.formatTwoDigits(7));
        assertEquals("99", StringUtil.formatTwoDigits(99));
        assertEquals("100", StringUtil.formatTwoDigits(100));
        assertEquals("00", StringUtil.formatTwoDigits(-1));
    }
}
