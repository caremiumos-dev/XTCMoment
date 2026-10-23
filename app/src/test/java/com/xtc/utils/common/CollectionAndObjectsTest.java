package com.xtc.utils.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CollectionAndObjectsTest {

    @Test
    public void collectionHelpersAreNullSafe() {
        assertTrue(CollectionUtil.isEmpty(null));
        assertTrue(CollectionUtil.isEmpty(Collections.emptyList()));
        assertFalse(CollectionUtil.isEmpty(Arrays.asList("one")));

        assertTrue(CollectionUtil.contains("b", Arrays.asList("a", "b")));
        assertFalse(CollectionUtil.contains("c", Arrays.asList("a", "b")));
        assertFalse(CollectionUtil.contains(null, Arrays.asList("a", "b")));
        assertFalse(CollectionUtil.contains("a", null));
    }

    @Test
    public void equalityHandlesNullsAndValues() {
        assertTrue(Objects.equals(null, null));
        assertFalse(Objects.equals(null, "x"));
        assertTrue(Objects.equals("x", "x"));
        assertEquals(0, Objects.hashCode(null));
        assertEquals("x".hashCode(), Objects.hashCode("x"));
    }

    @Test
    public void hashMatchesArrayHashSemantics() {
        assertEquals(Arrays.hashCode(new Object[]{"a", 2, true}),
                Objects.hash("a", 2, true));
    }

    @Test
    public void toStringUsesDefaultForNull() {
        assertEquals("null", Objects.toString(null));
        assertEquals("fallback", Objects.toString(null, "fallback"));
        assertEquals("value", Objects.toString("value", "fallback"));
    }

    @Test
    public void compareShortCircuitsIdenticalReferences() {
        Comparator<String> failingComparator = new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                throw new AssertionError("comparator should not be called");
            }
        };
        String value = "same";

        assertEquals(0, Objects.compare(value, value, failingComparator));
        assertEquals(-1, Objects.compare("a", "b", new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                return first.compareTo(second);
            }
        }));
    }

    @Test
    public void requireNonNullReturnsValueOrThrows() {
        String value = "value";
        assertSame(value, Objects.requireNonNull(value));
        assertSame(value, Objects.requireNonNull(value, "message"));

        try {
            Objects.requireNonNull(null);
            throw new AssertionError("expected NullPointerException");
        } catch (NullPointerException expected) {
            assertNull(expected.getMessage());
        }

        try {
            Objects.requireNonNull(null, "named");
            throw new AssertionError("expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("named", expected.getMessage());
        }
    }
}
