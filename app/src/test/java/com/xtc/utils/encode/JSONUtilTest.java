package com.xtc.utils.encode;

import com.xtc.moment.module.bean.Friend;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JSONUtilTest {

    @Test
    public void nullAndEmptyJsonDeserializeToNull() {
        assertNull(JSONUtil.toJSON(null));
        assertNull(JSONUtil.fromJSON(null, JsonBean.class));
        assertNull(JSONUtil.fromJSON("", JsonBean.class));
        assertNull(JSONUtil.fromJSON(null, List.class, Friend.class));
    }

    @Test
    public void beanRoundTripPreservesValues() {
        JsonBean source = new JsonBean();
        source.text = "好友圈";
        source.count = 7;
        source.enabled = true;

        JsonBean result = JSONUtil.fromJSON(JSONUtil.toJSON(source), JsonBean.class);

        assertNotNull(result);
        assertEquals(source.text, result.text);
        assertEquals(source.count, result.count);
        assertEquals(source.enabled, result.enabled);
    }

    @Test
    public void dateUsesEpochMilliseconds() {
        Date source = new Date(123456789L);
        String json = JSONUtil.toJSON(new DateHolder(source));
        DateHolder result = JSONUtil.fromJSON(json, DateHolder.class);

        assertEquals("{\"date\":123456789}", json);
        assertEquals(source, result.date);
    }

    @Test
    public void booleanAdapterAcceptsZeroAndOne() {
        BooleanBean zero = JSONUtil.fromJSON("{\"enabled\":0}", BooleanBean.class);
        BooleanBean one = JSONUtil.fromJSON("{\"enabled\":1}", BooleanBean.class);
        BooleanBean normal = JSONUtil.fromJSON("{\"enabled\":true}", BooleanBean.class);

        assertFalse(zero.enabled);
        assertTrue(one.enabled);
        assertTrue(normal.enabled);
    }

    @Test
    public void parameterizedCollectionDeserializationWorks() {
        String json = "[{\"watchId\":\"one\",\"name\":\"Alice\"},"
                + "{\"watchId\":\"two\",\"name\":\"Bob\"}]";

        List<Friend> friends = JSONUtil.fromJSON(json, List.class, Friend.class);

        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertEquals("one", friends.get(0).getWatchId());
        assertEquals("Bob", friends.get(1).getName());
    }

    private static final class JsonBean {
        private String text;
        private int count;
        private boolean enabled;
    }

    private static final class BooleanBean {
        private boolean enabled;
    }

    private static final class DateHolder {
        private Date date;

        private DateHolder() {
        }

        private DateHolder(Date date) {
            this.date = date;
        }
    }
}
