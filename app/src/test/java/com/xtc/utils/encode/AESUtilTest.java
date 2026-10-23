package com.xtc.utils.encode;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class AESUtilTest {

    private static final String KEY = "0123456789abcdef";

    @Test
    public void stringRoundTripSupportsUnicode() {
        String plain = "好友圈 AES \uD83D\uDE80";
        String encrypted = AESUtil.encryptToBase64(plain, KEY);

        assertEquals(plain, AESUtil.decryptFromBase64(encrypted, KEY));
    }

    @Test
    public void byteRoundTripPreservesBinaryData() {
        byte[] data = new byte[512];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 17);
        }

        String encrypted = AESUtil.encryptToBase64(data, KEY);

        assertArrayEquals(data, AESUtil.decryptFromBase64ToBytes(encrypted, KEY));
    }

    @Test
    public void outputMatchesStandardAesJce() throws Exception {
        String plain = "deterministic";
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES"));
        String expected = java.util.Base64.getEncoder().encodeToString(
                cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));

        assertEquals(expected, AESUtil.encryptToBase64(plain, KEY));
    }

    @Test
    public void encryptionIsDeterministicForEcbMode() {
        String first = AESUtil.encryptToBase64("same", KEY);
        String second = AESUtil.encryptToBase64("same", KEY);

        assertEquals(first, second);
    }

    @Test
    public void emptyStringStillRoundTrips() {
        String encrypted = AESUtil.encryptToBase64("", KEY);

        assertFalse(encrypted.isEmpty());
        assertEquals("", AESUtil.decryptFromBase64(encrypted, KEY));
    }

    @Test
    public void invalidKeysAndPayloadsReturnNull() {
        assertNull(AESUtil.encryptToBase64("text", "short"));
        assertNull(AESUtil.encryptToBase64("text", null));
        assertNull(AESUtil.decryptFromBase64("not-base64", KEY));
        assertNull(AESUtil.decryptFromBase64ToBytes("not-base64", KEY));
    }
}
