package com.xtc.utils.encode;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GzipUtilTest {

    @Test
    public void nullAndEmptyTextDoNotCompress() {
        assertNull(GzipUtil.compress(null, "UTF-8"));
        assertNull(GzipUtil.compress("", "UTF-8"));
        assertNull(GzipUtil.decompress(null));
        assertNull(GzipUtil.decompress(new byte[0]));
    }

    @Test
    public void utf8TextRoundTrips() {
        String text = "好友圈 \uD83D\uDE80 line 1\nline 2";
        byte[] compressed = GzipUtil.compress(text, "UTF-8");

        assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), GzipUtil.decompress(compressed));
    }

    @Test
    public void configuredCharsetIsUsed() {
        String text = "cafe";
        byte[] compressed = GzipUtil.compress(text, "US-ASCII");

        assertArrayEquals(text.getBytes(StandardCharsets.US_ASCII), GzipUtil.decompress(compressed));
    }

    @Test
    public void compressedDataUsesGzipMagicHeader() {
        byte[] compressed = GzipUtil.compress("payload", "UTF-8");

        assertEquals(0x1F, compressed[0] & 0xFF);
        assertEquals(0x8B, compressed[1] & 0xFF);
        assertFalse(compressed.length <= 10);
    }

    @Test
    public void invalidCompressedDataReturnsEmptyArray() {
        assertEquals(0, GzipUtil.decompress(new byte[]{1, 2, 3, 4}).length);
    }
}
