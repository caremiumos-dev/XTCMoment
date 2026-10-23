package com.xtc.utils.encode;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MD5UtilTest {

    @Test
    public void knownDigestsMatch() {
        assertEquals("D41D8CD98F00B204E9800998ECF8427E", MD5Util.md5(""));
        assertEquals("900150983CD24FB0D6963F7D28E17F72", MD5Util.md5("abc"));
        assertEquals("5D41402ABC4B2A76B9719D911017C592", MD5Util.md5("hello"));
    }

    @Test
    public void utf8TextMatchesJdkDigest() throws Exception {
        String text = "好友圈-test-\uD83D\uDE80";
        assertEquals(jdkMd5(text.getBytes(StandardCharsets.UTF_8)), MD5Util.md5(text));
    }

    @Test
    public void binaryDataMatchesJdkDigest() throws Exception {
        byte[] data = new byte[512];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 31);
        }
        assertEquals(jdkMd5(data), MD5Util.md5(data));
    }

    @Test
    public void nullByteArrayReturnsNull() {
        assertNull(MD5Util.md5((byte[]) null));
    }

    private static String jdkMd5(byte[] data) throws Exception {
        byte[] digest = MessageDigest.getInstance("MD5").digest(data);
        StringBuilder result = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            result.append(String.format("%02X", value & 0xFF));
        }
        return result.toString();
    }
}
