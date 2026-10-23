package com.xtc.moment.util;

import com.xtc.moment.db.bean.DbMoment;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MomentTypeUtilTest {

    @Test
    public void getMomentType_coversEveryMappedServerType() {
        Map<Integer, Integer> mappings = new LinkedHashMap<>();
        mappings.put(4, MomentTypeUtil.ITEM_TYPE_PHOTO);
        mappings.put(5, MomentTypeUtil.ITEM_TYPE_PHOTO);
        mappings.put(6, MomentTypeUtil.ITEM_TYPE_VIDEO);
        mappings.put(7, MomentTypeUtil.ITEM_TYPE_SHARE_TEXT);
        mappings.put(8, MomentTypeUtil.ITEM_TYPE_SHARE_IMAGE);
        mappings.put(9, MomentTypeUtil.ITEM_TYPE_SHARE_APP);
        mappings.put(11, MomentTypeUtil.ITEM_TYPE_OFFICIAL_PHOTO);
        mappings.put(12, MomentTypeUtil.ITEM_TYPE_OFFICIAL_PHOTO_TEXT);
        mappings.put(13, MomentTypeUtil.ITEM_TYPE_OFFICIAL_VIDEO);
        mappings.put(14, MomentTypeUtil.ITEM_TYPE_OFFICIAL_PHOTO_TEXT_H5);
        mappings.put(22, MomentTypeUtil.ITEM_TYPE_LIVE_PHOTO);
        mappings.put(23, MomentTypeUtil.ITEM_TYPE_SHARE_LIVE_PHOTO);
        mappings.put(24, MomentTypeUtil.ITEM_TYPE_SHARE_VIDEO);
        mappings.put(25, MomentTypeUtil.ITEM_TYPE_SHARE_H5);
        mappings.put(26, MomentTypeUtil.ITEM_TYPE_PHOTOS);
        mappings.put(27, MomentTypeUtil.ITEM_TYPE_VIDEO_TEXT);
        mappings.put(28, MomentTypeUtil.ITEM_TYPE_MULTI_SHARE_IMAGE_TEXT);

        for (Map.Entry<Integer, Integer> entry : mappings.entrySet()) {
            assertEquals("server type " + entry.getKey(), entry.getValue().intValue(),
                    MomentTypeUtil.getMomentType(moment(entry.getKey())));
        }
    }

    @Test
    public void getMomentType_unknownTypeUsesNormalPresentation() {
        assertEquals(MomentTypeUtil.ITEM_TYPE_NORMAL,
                MomentTypeUtil.getMomentType(moment(999)));
    }

    @Test
    public void classificationHelpersMatchProtocolTypes() {
        assertTrue(MomentTypeUtil.isCommonPhoto(5));
        assertTrue(MomentTypeUtil.isCommonPhoto(23));
        assertFalse(MomentTypeUtil.isCommonPhoto(6));

        assertTrue(MomentTypeUtil.isFunVideo(2));
        assertTrue(MomentTypeUtil.isPointVideo(1));
        assertTrue(MomentTypeUtil.isFunVideoOrPointVideo(1));
        assertTrue(MomentTypeUtil.isFunVideoOrPointVideo(2));
        assertFalse(MomentTypeUtil.isFunVideoOrPointVideo(3));

        assertTrue(MomentTypeUtil.isPhotoList(26));
        assertTrue(MomentTypeUtil.isSharePhoto(8));
        assertTrue(MomentTypeUtil.isSharePhoto(9));
        assertTrue(MomentTypeUtil.isSharePhoto(25));
        assertTrue(MomentTypeUtil.isShareVideo(24));
        assertTrue(MomentTypeUtil.isVideo(6));
        assertTrue(MomentTypeUtil.isVideoContent(27));
        assertTrue(MomentTypeUtil.isXTCOfficialType(1));
    }

    @Test
    public void officialTypeCoversTenThroughFourteenOnly() {
        for (int type = 10; type <= 14; type++) {
            assertTrue("type " + type, MomentTypeUtil.isOfficialType(type));
        }
        assertFalse(MomentTypeUtil.isOfficialType(9));
        assertFalse(MomentTypeUtil.isOfficialType(15));
    }

    @Test
    public void ableChangeVisibleRange_excludesServerRestrictedTypes() {
        for (int type : new int[]{11, 12, 13, 14, 25}) {
            assertFalse("type " + type, MomentTypeUtil.ableChangeVisibleRangeType(type));
        }
        for (int type : new int[]{1, 4, 6, 24, 26}) {
            assertTrue("type " + type, MomentTypeUtil.ableChangeVisibleRangeType(type));
        }
    }

    @Test
    public void isOfficialTypeMoment_handlesNullAndShortIds() {
        assertFalse(MomentTypeUtil.isOfficialType((DbMoment) null));
        assertFalse(MomentTypeUtil.isOfficialType(moment(1)));

        DbMoment shortId = moment(1);
        shortId.setWatchId("official");
        assertTrue(MomentTypeUtil.isOfficialType(shortId));

        DbMoment longId = moment(1);
        longId.setWatchId("1234567890123456789012345678901234567890");
        assertFalse(MomentTypeUtil.isOfficialType(longId));
        longId.setType(12);
        assertTrue(MomentTypeUtil.isOfficialType(longId));
    }

    @Test
    public void funVideoMoment_parsesNestedModelType() {
        DbMoment funVideo = moment(24);
        funVideo.setContent("{\"funVideoParam\":{\"modelType\":2}}");
        assertTrue(MomentTypeUtil.checkMomentIsFunVideo(funVideo));

        DbMoment pointVideo = moment(24);
        pointVideo.setContent("{\"funVideoParam\":{\"modelType\":1}}");
        assertTrue(MomentTypeUtil.checkMomentIsFunVideo(pointVideo));

        DbMoment regularVideo = moment(24);
        regularVideo.setContent("{\"funVideoParam\":{\"modelType\":3}}");
        assertFalse(MomentTypeUtil.checkMomentIsFunVideo(regularVideo));

        DbMoment otherType = moment(6);
        otherType.setContent("{\"funVideoParam\":{\"modelType\":2}}");
        assertFalse(MomentTypeUtil.checkMomentIsFunVideo(otherType));
    }

    @Test
    public void officialTopMoment_requiresOfficialTypeTopFlagAndFutureExpiry() {
        DbMoment moment = moment(12);
        moment.setTop(1);
        moment.setTopExpireTime(System.currentTimeMillis() + 60_000L);
        assertTrue(OfficialMomentTypeUtil.isTopOfficialMoment(moment));

        moment.setTop(0);
        assertFalse(OfficialMomentTypeUtil.isTopOfficialMoment(moment));

        moment.setTop(1);
        moment.setTopExpireTime(System.currentTimeMillis() - 1L);
        assertFalse(OfficialMomentTypeUtil.isTopOfficialMoment(moment));

        DbMoment normal = moment(1);
        normal.setTop(1);
        normal.setTopExpireTime(System.currentTimeMillis() + 60_000L);
        assertFalse(OfficialMomentTypeUtil.isTopOfficialMoment(normal));
    }

    private static DbMoment moment(int type) {
        DbMoment moment = new DbMoment();
        moment.setType(type);
        return moment;
    }
}
