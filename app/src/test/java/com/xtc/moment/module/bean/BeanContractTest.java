package com.xtc.moment.module.bean;

import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.net.bean.NormalResultBean;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BeanContractTest {

    @Test
    public void friendGettersSettersAndToStringRemainStable() {
        Friend friend = new Friend();
        friend.setWatchId("watch-1");
        friend.setName("Alice");
        friend.setFriendIcon("avatar.png");

        assertEquals("watch-1", friend.getWatchId());
        assertEquals("Alice", friend.getName());
        assertEquals("avatar.png", friend.getFriendIcon());
        assertTrue(friend.toString().contains("watchId='watch-1'"));
        assertTrue(friend.toString().contains("name='Alice'"));
    }

    @Test
    public void visibleRequestBeanPreservesListIdentity() {
        FriendsVisibleBeanReq request = new FriendsVisibleBeanReq();
        java.util.List<String> ids = Arrays.asList("one", "two");

        request.setMomentIds(ids);

        assertSame(ids, request.getMomentIds());
        assertTrue(request.toString().contains("one"));
    }

    @Test
    public void genericResponseEnvelopeMapsAllFields() {
        NormalResultBean bean = new NormalResultBean();
        Object data = new Object();
        bean.setCode("000001");
        bean.setDesc("ok");
        bean.setData(data);

        assertEquals("000001", bean.getCode());
        assertEquals("ok", bean.getDesc());
        assertSame(data, bean.getData());
    }

    @Test
    public void defaultResponseMapsLikeAndCommentMetadata() {
        DefaultResponse response = new DefaultResponse();
        response.setMomentId("moment-1");
        response.setMomentWatchId("owner-1");
        response.setWatchId("actor-1");
        response.setWatchName("Bob");
        response.setCreateTime(123L);
        response.setEmotionId(7);

        assertEquals("moment-1", response.getMomentId());
        assertEquals("owner-1", response.getMomentWatchId());
        assertEquals("actor-1", response.getWatchId());
        assertEquals("Bob", response.getWatchName());
        assertEquals(123L, response.getCreateTime());
        assertEquals(7, response.getEmotionId());
        assertTrue(response.toString().contains("watchName='Bob'"));
    }
}
