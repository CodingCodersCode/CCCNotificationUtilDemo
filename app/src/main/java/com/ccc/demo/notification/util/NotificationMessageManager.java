package com.ccc.demo.notification.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ccc.lib.notification.util.CCCNotificationUtil;
import com.ccc.lib.notification.util.log.LogUtil;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Date：2018/7/26 15:54
 * <p>
 * author: CodingCodersCode
 */
public class NotificationMessageManager {

    private final String LOG_TAG = getClass().getCanonicalName();

    //本次启动通知的notification id列表
    private final LinkedList<String> mNotificationIdToUseList = new LinkedList<String>();

    private static final int DEFAULT_NOTIFICATION_START_ID_SHOW = 35000;

    private static final int DEFAULT_MAX_COUNT_TO_SHOW = 5;

    private CCCNotificationUtil mCCCNotificationUtil;

    private SharedPreferences mSharedPreferences;

    private static final String SPK_NOTIFICATION_ID_TO_USE = "spk_notification_id_to_use";

    private boolean hasInited = false;

    private NotificationMessageManager() {

    }

    private static class NotificationMessageManagerHolder {
        public static NotificationMessageManager instance = new NotificationMessageManager();
    }

    public static NotificationMessageManager getInstance(Context context) {
        NotificationMessageManager notificationMessageManager = NotificationMessageManagerHolder.instance;
        notificationMessageManager.init(context);

        return notificationMessageManager;
    }

    public void init(Context context) {

        if (hasInited) {
            return;
        }

        mCCCNotificationUtil = CCCNotificationUtil.getInstance(context, NotificationMessageReceiver.class);

        this.mSharedPreferences = context.getSharedPreferences("NotificationIdToUse", Context.MODE_PRIVATE);

        onCreateNotificationIdList();

    }

    /**
     * 创建通知id数组列表
     */
    private void onCreateNotificationIdList() {
        int startId;
        String[] notificationIdToUseArr;
        String notificationIdsToUse;
        try {

            notificationIdsToUse = onReadNotificationIdsFromSp();

            if (TextUtils.isEmpty(notificationIdsToUse)) {
                notificationIdToUseArr = onCreateDefaultNotificationIdsToUse();
            } else {
                notificationIdToUseArr = notificationIdsToUse.split("-");
            }

            if (notificationIdToUseArr != null) {
                this.mNotificationIdToUseList.addAll(Arrays.asList(notificationIdToUseArr));
            }

            if (this.mNotificationIdToUseList.size() <= DEFAULT_MAX_COUNT_TO_SHOW) {
                startId = DEFAULT_NOTIFICATION_START_ID_SHOW;
                while (true) {
                    if (this.mNotificationIdToUseList.size() <= DEFAULT_MAX_COUNT_TO_SHOW) {
                        if (this.mNotificationIdToUseList.contains(String.valueOf(startId))) {

                        } else {
                            this.mNotificationIdToUseList.add(String.valueOf(startId));
                        }
                        startId++;
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建通知id数组列表发生异常，详情见异常信息", e);
        }
    }

    /**
     * 创建默认的通知id数组
     *
     * @return
     */
    private String[] onCreateDefaultNotificationIdsToUse() {
        String[] defaultIdsToUse = new String[DEFAULT_MAX_COUNT_TO_SHOW];
        int index;
        int startId = DEFAULT_NOTIFICATION_START_ID_SHOW;
        try {
            for (index = 0; index < DEFAULT_MAX_COUNT_TO_SHOW; index++, startId++) {
                defaultIdsToUse[index] = String.valueOf(startId);
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建默认的通知id数组发生异常，详情见异常信息", e);
        }
        return defaultIdsToUse;
    }

    /**
     * 将已删除通知id添加入id列表头部
     *
     * @param deletedId
     */
    public void addDeletedNotificationIdToFirst(int deletedId) {
        try {
            synchronized (this.mNotificationIdToUseList) {
                mNotificationIdToUseList.remove(String.valueOf(deletedId));
                mNotificationIdToUseList.addFirst(String.valueOf(deletedId));

                onWriteNotificationIdsToSp(mNotificationIdToUseList.toString());
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "将已删除通知id添加入id列表头部发生异常，详情见异常信息", e);
        }

    }

    /**
     * 将已点击通知id添加入id列表头部
     *
     * @param clickedId
     */
    public void addClickedNotificationIdToFirst(int clickedId) {
        try {
            synchronized (this.mNotificationIdToUseList) {
                mNotificationIdToUseList.remove(String.valueOf(clickedId));
                mNotificationIdToUseList.addFirst(String.valueOf(clickedId));

                onWriteNotificationIdsToSp(mNotificationIdToUseList.toString());
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "将已点击通知id添加入id列表头部发生异常，详情见异常信息", e);
        }
    }

    private synchronized void onWriteNotificationIdsToSp(String ids) {
        SharedPreferences.Editor editor;
        try {
            synchronized (this.mSharedPreferences) {
                editor = this.mSharedPreferences.edit();

                editor.putString(SPK_NOTIFICATION_ID_TO_USE, requireNonNull(ids));

                editor.apply();
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "存储通知id列表发生异常，详情见异常信息", e);
        }
    }

    private synchronized String onReadNotificationIdsFromSp() {
        String ids = null;
        try {
            synchronized (this.mSharedPreferences) {
                ids = this.mSharedPreferences.getString(SPK_NOTIFICATION_ID_TO_USE, "");
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "获取通知id列表发生异常，详情见异常信息", e);
        }
        return ids;
    }

    private String requireNonNull(String origin) {
        if (TextUtils.isEmpty(origin)) {
            return "";
        } else {
            return origin;
        }
    }

    /**
     * 获取通知唯一id标识
     *
     * @return
     */
    private int getUniqueNotificationId() {
        String newNotificationId = String.valueOf(DEFAULT_NOTIFICATION_START_ID_SHOW);
        try {
            synchronized (this.mNotificationIdToUseList) {
                newNotificationId = this.mNotificationIdToUseList.removeFirst();
                this.mNotificationIdToUseList.addLast(newNotificationId);

                onWriteNotificationIdsToSp(this.mNotificationIdToUseList.toString());
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "获取通知id发生异常，详情见异常信息", e);
        }
        return Integer.valueOf(newNotificationId);
    }

    public void notify(Context context, String title, String content, Parcelable notificationDataBean) {

        int idThisTime = getUniqueNotificationId();

        LogUtil.printLog("e", LOG_TAG, "显示通知：id=" + idThisTime + ",title = " + title + ", content = " + content);

        this.mCCCNotificationUtil.notify(context, idThisTime, title, content, notificationDataBean);
    }

}
