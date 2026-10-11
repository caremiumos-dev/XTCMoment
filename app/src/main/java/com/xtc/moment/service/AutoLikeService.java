package com.xtc.moment.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.Moments;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.utils.storage.SharedManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 自动点赞/评论后台服务。
 * 定时轮询好友圈动态，对未点赞的动态自动点赞并可选自动评论。
 */
public class AutoLikeService extends Service {

    private static final String TAG = "AutoLikeService";
    private static final long POLL_INTERVAL_MS = 30_000L; // 30秒轮询一次
    private static final long LIKE_DELAY_MS = 2_000L;     // 点赞间隔2秒，模拟人工

    private Handler handler;
    private MomentHttpServiceProxy proxy;
    private String watchId;
    private final Random random = new Random();

    private final List<String> defaultComments = new ArrayList<String>() {{
        add("赞！");
        add("不错哦~");
        add("厉害了");
        add("好看！");
        add("哈哈");
    }};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        proxy = new MomentHttpServiceProxy(this);
        watchId = AccountInfoServerImpl.getInstance(this).getWatchAccountInfo().getWatchId(this);
        LogUtil.i(TAG, "AutoLikeService created, watchId=" + watchId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isEnabled()) {
            LogUtil.i(TAG, "AutoLike disabled, stop service");
            stopSelf();
            return START_NOT_STICKY;
        }
        scheduleNextPoll();
        return START_STICKY;
    }

    private boolean isEnabled() {
        return SharedManager.getInstance(this).getBoolean("auto_like_enabled", false);
    }

    private void scheduleNextPoll() {
        if (!isEnabled()) {
            stopSelf();
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollAndLike();
            }
        }, POLL_INTERVAL_MS);
    }

    /**
     * 轮询最新动态并执行点赞/评论。
     */
    private void pollAndLike() {
        long end = System.currentTimeMillis();
        long begin = end - TimeUnit.HOURS.toMillis(24); // 最近24小时

        SearchMomentBody body = new SearchMomentBody();
        body.setBegin(begin);
        body.setEnd(end);
        body.setFriend(1); // 全部好友
        body.setFrom(0);
        body.setSize(20);
        body.setWatchId(watchId);
        body.setCurrentWatchId(watchId);
        body.setCommentPageSize(5);

        proxy.searchMoment(0, end, 20, watchId, watchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Moments>() {
                    @Override
                    public void onCompleted() {
                        scheduleNextPoll();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "searchMoment error: " + e.getMessage());
                        scheduleNextPoll();
                    }

                    @Override
                    public void onNext(Moments moments) {
                        if (moments == null || moments.getWatchMoments() == null) return;
                        processMoments(moments.getWatchMoments());
                    }
                });
    }

    private void processMoments(List<DbMoment> list) {
        int liked = 0;
        for (final DbMoment m : list) {
            if (m == null) continue;

            // 延迟点赞，模拟人工浏览间隔
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doLike(m);
                }
            }, liked * LIKE_DELAY_MS);

            liked++;
            if (liked >= 5) break; // 每轮最多赞5条
        }
    }

    private void doLike(final DbMoment moment) {
        proxy.praiseMoment(moment.getMomentId(), moment.getWatchId(), watchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<com.xtc.moment.net.bean.DefaultResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "like failed: " + e.getMessage());
                    }

                    @Override
                    public void onNext(com.xtc.moment.net.bean.DefaultResponse response) {
                        LogUtil.i(TAG, "liked moment " + moment.getMomentId());
                        // 点赞成功后可选自动评论
                        if (isAutoCommentEnabled() && shouldComment()) {
                            doComment(moment);
                        }
                    }
                });
    }

    private void doComment(final DbMoment moment) {
        String comment = pickComment();
        if (TextUtils.isEmpty(comment)) return;

        DbMomentComment dbComment = new DbMomentComment();
        dbComment.setMomentId(moment.getMomentId());
        dbComment.setMomentWatchId(moment.getWatchId());
        dbComment.setWatchId(watchId);
        dbComment.setComment(comment);
        dbComment.setType(DbMomentComment.TYPE_COMMENT);

        proxy.commentMoment(dbComment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<com.xtc.moment.net.bean.CommentResultBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "comment failed: " + e.getMessage());
                    }

                    @Override
                    public void onNext(com.xtc.moment.net.bean.CommentResultBean response) {
                        LogUtil.i(TAG, "commented moment " + moment.getMomentId());
                    }
                });
    }

    private boolean isAutoCommentEnabled() {
        return SharedManager.getInstance(this).getBoolean("auto_comment_enabled", false);
    }

    private boolean shouldComment() {
        // 30% 概率评论
        return random.nextInt(100) < 30;
    }

    private String pickComment() {
        if (defaultComments.isEmpty()) return "赞！";
        return defaultComments.get(random.nextInt(defaultComments.size()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        LogUtil.i(TAG, "AutoLikeService destroyed");
    }
}
