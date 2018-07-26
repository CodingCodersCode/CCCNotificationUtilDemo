package com.ccc.demo.notification.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ccc.lib.notification.util.CCCNotificationUtil;
import com.ccc.lib.notification.util.log.LogUtil;

/**
 * Date：2018/7/26 15:48
 * <p>
 * author: CodingCodersCode
 */
public class NotificationMessageReceiver extends BroadcastReceiver {

    private String TAG = getClass().getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (context == null || intent == null) {
                return;
            }
            switch (intent.getAction()) {
                case CCCNotificationUtil.ACTION_NOTIFICATION_GROUP_CHILD_DELETE:
                    LogUtil.printLog("e", TAG, "监听到了通知删除的操作");
                    onProcessNotificationDeleteEvent(context, intent);
                    break;
                case CCCNotificationUtil.ACTION_NOTIFICATION_GROUP_CHILD_CLICKED:
                    LogUtil.printLog("e", TAG, "监听到了通知点击的操作");
                    onProcessNotificationClickedEvent(context, intent);
                    break;
                case CCCNotificationUtil.ACTION_NOTIFICATION_GROUP_SUMMARY_CLICKED:
                    LogUtil.printLog("e", TAG, "监听到了通知分组点击的操作");
                    onProcessNotificationGroupSummaryClickedEvent(context);
                    break;
                default:

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理通知删除事件
     *
     * @param context
     * @param intent
     */
    private void onProcessNotificationDeleteEvent(Context context, Intent intent) {
        int deletedNotificationId;
        Bundle dataBundle;
        try {
            dataBundle = intent.getExtras();

            if (dataBundle != null) {
                deletedNotificationId = dataBundle.getInt(CCCNotificationUtil.IK_NOTIFICATION_ID, -1);
                if (deletedNotificationId != -1) {
                    NotificationMessageManager.getInstance(context).addDeletedNotificationIdToFirst(deletedNotificationId);
                }
            }
        } catch (Exception e) {
            LogUtil.printLog("e", TAG, "处理通知删除事件发生异常，详情见异常信息", e);
        }
    }

    /**
     * 处理通知点击事件
     *
     * @param context
     * @param intent
     */
    private void onProcessNotificationClickedEvent(Context context, Intent intent) {
        int clickedNotificationId;
        Intent detailIntent;
        Bundle detailDataBundle;
        Bundle dataBundle;
        try {
            dataBundle = intent.getExtras();

            if (dataBundle != null) {
                clickedNotificationId = dataBundle.getInt(CCCNotificationUtil.IK_NOTIFICATION_ID, -1);
                if (clickedNotificationId != -1) {
                    NotificationMessageManager.getInstance(context).addClickedNotificationIdToFirst(clickedNotificationId);
                }
            }

            detailDataBundle = new Bundle();
            detailDataBundle.putAll(dataBundle);

            detailIntent = new Intent(context, DetailActivity.class);

            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            detailIntent.putExtra("IK_TARGET_ACTIVITY_CANONICAL_CLASS_NAME", DetailActivity.class.getCanonicalName());
            detailIntent.putExtras(detailDataBundle);
            context.startActivity(detailIntent);
        } catch (Exception e) {
            LogUtil.printLog("e", TAG, "处理通知点击事件发生异常，详情见异常信息", e);
        }
    }

    /**
     * 处理通知分组点击事件
     *
     * @param context
     */
    private void onProcessNotificationGroupSummaryClickedEvent(Context context) {
        Intent intent;
        try {
            intent = new Intent(context, MsgListActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
