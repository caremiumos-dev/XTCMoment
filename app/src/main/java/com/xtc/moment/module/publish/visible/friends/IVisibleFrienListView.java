package com.xtc.moment.module.publish.visible.friends;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.module.bean.Friend;

import java.util.List;

/**
 * 可见好友列表视图接口。
 */
public interface IVisibleFrienListView extends MvpView {

    /** 服务端好友列表加载完成（失败时 Presenter 已回退 provider）。 */
    void showFriends(List<Friend> friends);
}
