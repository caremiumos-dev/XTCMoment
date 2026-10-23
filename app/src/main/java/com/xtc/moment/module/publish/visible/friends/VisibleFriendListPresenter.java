package com.xtc.moment.module.publish.visible.friends;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.IFriendInfoServe;
import com.xtc.moment.util.ContactUtil;
import com.xtc.moment.util.HandlerUtil;

import java.util.List;

/**
 * Presenter of the "visible to friends" page. Server friends take priority;
 * the local contact provider is used only when the server has no usable list.
 */
public class VisibleFriendListPresenter extends MvpPresenter<IVisibleFrienListView> {

    private static final String TAG = "VisibleTypePresenter";

    private final Context context;
    private final ContactManager contactManager;
    private final IFriendInfoServe friendInfoServe;

    public VisibleFriendListPresenter(Context context) {
        this.context = context;
        this.contactManager = ContactManager.getInstance(context);
        this.friendInfoServe = FriendInfoServeImpl.getInstance(context);
    }

    /**
     * 优先使用服务端好友列表，服务端失败或返回空列表时回退通讯录 provider。
     * 网络请求放到后台，避免打开可见范围页面时阻塞主线程。
     */
    public void loadAllFriend() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                List<Friend> friends = friendInfoServe.getAllFriendInfo();
                if (friends == null || friends.isEmpty()) {
                    friends = ContactUtil.convertToFriendWithPhoneAccount(
                            contactManager.getAllContactsSync());
                }
                final List<Friend> result = friends;
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            getView().showFriends(result);
                        }
                    }
                });
            }
        });
    }
}
