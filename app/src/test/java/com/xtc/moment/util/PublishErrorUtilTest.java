package com.xtc.moment.util;

import com.xtc.moment.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PublishErrorUtilTest {

    @Test
    public void unknownOrMissingCodesUseGenericPublishFailure() {
        assertEquals(R.string.publish_fail, PublishErrorUtil.getFailMessageRes(null));
        assertEquals(R.string.publish_fail, PublishErrorUtil.getFailMessageRes(""));
        assertEquals(R.string.publish_fail, PublishErrorUtil.getFailMessageRes("000002"));
        assertEquals(R.string.publish_fail, PublishErrorUtil.getFailMessageRes("unexpected"));
    }

    @Test
    public void requestCodesUseFrequentRequestMessage() {
        assertEquals(R.string.frequent_request,
                PublishErrorUtil.getFailMessageRes("1002"));
        assertEquals(R.string.frequent_request,
                PublishErrorUtil.getFailMessageRes("server=1003"));
    }

    @Test
    public void suspendedAccountUsesAbnormalAccountMessage() {
        assertEquals(R.string.account_abnormal,
                PublishErrorUtil.getFailMessageRes("000007"));
        assertEquals(R.string.account_abnormal,
                PublishErrorUtil.getFailMessageRes("code=000007,tips=suspended"));
    }

    @Test
    public void isAccountBannedRecognizesOnlyTheSuspensionCode() {
        assertTrue(PublishErrorUtil.isAccountBanned("000007"));
        assertTrue(PublishErrorUtil.isAccountBanned("prefix-000007-suffix"));
        assertFalse(PublishErrorUtil.isAccountBanned(null));
        assertFalse(PublishErrorUtil.isAccountBanned(""));
        assertFalse(PublishErrorUtil.isAccountBanned("000002"));
        assertFalse(PublishErrorUtil.isAccountBanned("1003"));
    }
}
