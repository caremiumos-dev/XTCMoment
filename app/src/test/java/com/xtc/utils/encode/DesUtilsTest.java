package com.xtc.utils.encode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class DesUtilsTest {

    @Test
    public void textRoundTripsThroughHex() throws Exception {
        DesUtils des = new DesUtils();
        String plain = "好友圈 DES";

        String encrypted = des.encryptToHex(plain);

        assertNotEquals(plain, encrypted);
        assertFalse(encrypted.isEmpty());
        assertEquals(plain, des.decryptFromHex(encrypted));
    }

    @Test
    public void emptyHexDecryptsToEmptyString() throws Exception {
        DesUtils des = new DesUtils();

        assertEquals("", des.decryptFromHex(""));
        assertEquals("", des.decryptFromHex(null));
    }

    @Test
    public void encryptionIsDeterministicForEcbMode() throws Exception {
        DesUtils des = new DesUtils();

        assertEquals(des.encryptToHex("same"), des.encryptToHex("same"));
    }
}
