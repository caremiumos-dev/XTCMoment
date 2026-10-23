package com.xtc.utils.encode;

import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RandomValueTest {

    @Test
    public void randomLettersUsesOnlyLowercaseAscii() {
        String value = RandomUtils.randomLetters(new Random(42L), 128);

        assertEquals(128, value.length());
        assertTrue(value.matches("[a-z]{128}"));
        assertEquals("", RandomUtils.randomLetters(new Random(42L), 0));
    }

    @Test
    public void uuidIsCompactLowercaseHex() {
        String value = UUIDUtil.randomUUID();

        assertEquals(32, value.length());
        assertTrue(value.matches("[0-9a-f]{32}"));
    }

    @Test
    public void uuidsAreOverwhelminglyUnique() {
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 256; i++) {
            values.add(UUIDUtil.randomUUID());
        }

        assertEquals(256, values.size());
        assertNotEquals(UUIDUtil.randomUUID(), UUIDUtil.randomUUID());
    }
}
