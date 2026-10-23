package com.xtc.moment.util;

import com.xtc.moment.module.bean.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友列表来源策略：服务端优先，缺失时由调用方提供本机通讯录回退。
 */
public final class FriendListPolicy {

    /** Loads the local provider fallback list. */
    public interface FriendProvider {
        List<Friend> load();
    }

    /** Looks up one local contact by friend watch id. */
    public interface ContactFallback {
        Friend find(String watchId);
    }

    private FriendListPolicy() {
    }

    /**
     * Returns the non-empty server list, otherwise loads the provider list.
     * The returned fallback value is preserved exactly for compatibility with
     * callers that distinguish null from an empty list.
     */
    public static List<Friend> serverOrProvider(List<Friend> serverFriends,
                                                FriendProvider provider) {
        if (serverFriends != null && !serverFriends.isEmpty()) {
            return serverFriends;
        }
        return provider == null ? null : provider.load();
    }

    /**
     * Resolves watch ids in request order. A server match wins; only missing
     * ids are looked up in the local provider. Invalid ids and unresolved
     * contacts are omitted.
     */
    public static List<Friend> resolveWatchIds(List<Friend> availableFriends,
                                               List<String> watchIds,
                                               ContactFallback provider) {
        ArrayList<Friend> result = new ArrayList<>();
        if (watchIds == null || watchIds.isEmpty()) {
            return result;
        }

        Map<String, Friend> indexedFriends = new HashMap<>();
        if (availableFriends != null) {
            for (Friend friend : availableFriends) {
                if (friend != null && !isEmpty(friend.getWatchId())) {
                    indexedFriends.put(friend.getWatchId(), friend);
                }
            }
        }

        for (String watchId : watchIds) {
            if (isEmpty(watchId)) {
                continue;
            }
            Friend friend = indexedFriends.get(watchId);
            if (friend == null && provider != null) {
                friend = provider.find(watchId);
            }
            if (friend != null) {
                result.add(friend);
            }
        }
        return result;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
