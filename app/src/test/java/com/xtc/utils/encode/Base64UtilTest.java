package com.xtc.utils.encode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Base64UtilTest {

    @Test
    public void encode_matchesRfc4648Vectors() {
        assertEquals("", Base64Util.encode(new byte[0]));
        assertEquals("Zg==", Base64Util.encode(bytes('f')));
        assertEquals("Zm8=", Base64Util.encode(bytes('f', 'o')));
        assertEquals("Zm9v", Base64Util.encode(bytes('f', 'o', 'o')));
        assertEquals("Zm9vYg==", Base64Util.encode(bytes('f', 'o', 'o', 'b')));
        assertEquals("Zm9vYmE=", Base64Util.encode(bytes('f', 'o', 'o', 'b', 'a')));
        assertEquals("Zm9vYmFy", Base64Util.encode(bytes('f', 'o', 'o', 'b', 'a', 'r')));
    }

    @Test
    public void decode_matchesRfc4648Vectors() {
        assertArrayEquals(new byte[0], Base64Util.decode(""));
        assertArrayEquals(bytes('f'), Base64Util.decode("Zg=="));
        assertArrayEquals(bytes('f', 'o'), Base64Util.decode("Zm8="));
        assertArrayEquals(bytes('f', 'o', 'o'), Base64Util.decode("Zm9v"));
        assertArrayEquals(bytes('f', 'o', 'o', 'b'), Base64Util.decode("Zm9vYg=="));
        assertArrayEquals(bytes('f', 'o', 'o', 'b', 'a'), Base64Util.decode("Zm9vYmE="));
        assertArrayEquals(bytes('f', 'o', 'o', 'b', 'a', 'r'), Base64Util.decode("Zm9vYmFy"));
    }

    @Test
    public void decode_ignoresWhitespace() {
        assertArrayEquals(bytes('f', 'o', 'o', 'b', 'a', 'r'),
                Base64Util.decode(" Zm9v\nYmFy\t"));
    }

    @Test
    public void decode_rejectsMalformedInput() {
        assertNull(Base64Util.decode("A"));
        assertNull(Base64Util.decode("TQ="));
        assertNull(Base64Util.decode("TR=="));
        assertNull(Base64Util.decode("TW$="));
        assertNull(Base64Util.decode("TQ==="));
    }

    @Test
    public void nullInputsReturnNull() {
        assertNull(Base64Util.encode(null));
        assertNull(Base64Util.decode(null));
    }

    @Test
    public void randomBinaryRoundTripMatchesJdkBase64() {
        Random random = new Random(20261023L);
        for (int length = 0; length <= 256; length++) {
            byte[] data = new byte[length];
            random.nextBytes(data);

            String encoded = Base64Util.encode(data);
            assertEquals(Base64.getEncoder().encodeToString(data), encoded);
            assertArrayEquals(data, Base64Util.decode(encoded));
        }
    }

    @Test
    public void allSingleByteValuesRoundTrip() {
        for (int value = 0; value <= 255; value++) {
            byte[] data = new byte[]{(byte) value};
            assertArrayEquals(data, Base64Util.decode(Base64Util.encode(data)));
        }
    }

    private static byte[] bytes(char... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }
}
