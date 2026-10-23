package com.xtc.moment.util;

import com.xtc.moment.module.bean.Friend;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FriendListPolicyTest {

    @Test
    public void serverOrProvider_returnsServerListWithoutLoadingProvider() {
        final int[] calls = {0};
        List<Friend> server = Arrays.asList(friend("server", "Server"));

        List<Friend> result = FriendListPolicy.serverOrProvider(server,
                new FriendListPolicy.FriendProvider() {
                    @Override
                    public List<Friend> load() {
                        calls[0]++;
                        return Collections.singletonList(friend("provider", "Provider"));
                    }
                });

        assertSame(server, result);
        assertEquals(0, calls[0]);
    }

    @Test
    public void serverOrProvider_loadsProviderForNullServerList() {
        List<Friend> provider = Collections.singletonList(friend("provider", "Provider"));

        List<Friend> result = FriendListPolicy.serverOrProvider(null, provider(provider));

        assertSame(provider, result);
    }

    @Test
    public void serverOrProvider_loadsProviderForEmptyServerList() {
        List<Friend> provider = Collections.singletonList(friend("provider", "Provider"));

        List<Friend> result = FriendListPolicy.serverOrProvider(
                Collections.<Friend>emptyList(), provider(provider));

        assertSame(provider, result);
    }

    @Test
    public void serverOrProvider_preservesNullWhenNoProviderExists() {
        assertNull(FriendListPolicy.serverOrProvider(null, null));
    }

    @Test
    public void resolveWatchIds_prefersServerAndPreservesRequestOrder() {
        List<Friend> available = Arrays.asList(
                friend("watch-b", "Server B"),
                friend("watch-a", "Server A"));
        final List<String> providerCalls = new ArrayList<>();

        List<Friend> result = FriendListPolicy.resolveWatchIds(available,
                Arrays.asList("watch-a", "watch-b"),
                new FriendListPolicy.ContactFallback() {
                    @Override
                    public Friend find(String watchId) {
                        providerCalls.add(watchId);
                        return friend(watchId, "Provider " + watchId);
                    }
                });

        assertEquals(Arrays.asList("watch-a", "watch-b"), watchIds(result));
        assertEquals(Arrays.asList("Server A", "Server B"), names(result));
        assertTrue(providerCalls.isEmpty());
    }

    @Test
    public void resolveWatchIds_onlyFallsBackForMissingContact() {
        List<Friend> available = Collections.singletonList(friend("server", "Server"));
        final List<String> providerCalls = new ArrayList<>();

        List<Friend> result = FriendListPolicy.resolveWatchIds(available,
                Arrays.asList("server", "provider"),
                new FriendListPolicy.ContactFallback() {
                    @Override
                    public Friend find(String watchId) {
                        providerCalls.add(watchId);
                        return friend(watchId, "Provider");
                    }
                });

        assertEquals(Arrays.asList("server", "provider"), watchIds(result));
        assertEquals(Collections.singletonList("provider"), providerCalls);
    }

    @Test
    public void resolveWatchIds_skipsEmptyAndUnresolvedIds() {
        List<Friend> result = FriendListPolicy.resolveWatchIds(
                Collections.singletonList(friend("server", "Server")),
                Arrays.asList("", null, "missing", "server"),
                new FriendListPolicy.ContactFallback() {
                    @Override
                    public Friend find(String watchId) {
                        return null;
                    }
                });

        assertEquals(Collections.singletonList("server"), watchIds(result));
    }

    @Test
    public void resolveWatchIds_supportsServerOnlyWithoutProvider() {
        List<Friend> result = FriendListPolicy.resolveWatchIds(
                Collections.singletonList(friend("server", "Server")),
                Arrays.asList("server", "missing"), null);

        assertEquals(Collections.singletonList("server"), watchIds(result));
    }

    @Test
    public void resolveWatchIds_loadsEveryIdWhenServerListIsNull() {
        List<Friend> result = FriendListPolicy.resolveWatchIds(null,
                Arrays.asList("one", "two"),
                new FriendListPolicy.ContactFallback() {
                    @Override
                    public Friend find(String watchId) {
                        return friend(watchId, watchId);
                    }
                });

        assertEquals(Arrays.asList("one", "two"), watchIds(result));
    }

    @Test
    public void resolveWatchIds_preservesDuplicateRequestedIds() {
        List<Friend> result = FriendListPolicy.resolveWatchIds(
                Collections.singletonList(friend("server", "Server")),
                Arrays.asList("server", "server"), null);

        assertEquals(Arrays.asList("server", "server"), watchIds(result));
    }

    @Test
    public void resolveWatchIds_ignoresNullOrEmptyRequest() {
        assertTrue(FriendListPolicy.resolveWatchIds(null, null, null).isEmpty());
        assertTrue(FriendListPolicy.resolveWatchIds(null,
                Collections.<String>emptyList(), null).isEmpty());
    }

    private static FriendListPolicy.FriendProvider provider(final List<Friend> result) {
        return new FriendListPolicy.FriendProvider() {
            @Override
            public List<Friend> load() {
                return result;
            }
        };
    }

    private static Friend friend(String watchId, String name) {
        Friend friend = new Friend();
        friend.setWatchId(watchId);
        friend.setName(name);
        return friend;
    }

    private static List<String> watchIds(List<Friend> friends) {
        List<String> result = new ArrayList<>();
        for (Friend friend : friends) {
            result.add(friend.getWatchId());
        }
        return result;
    }

    private static List<String> names(List<Friend> friends) {
        List<String> result = new ArrayList<>();
        for (Friend friend : friends) {
            result.add(friend.getName());
        }
        return result;
    }
}
