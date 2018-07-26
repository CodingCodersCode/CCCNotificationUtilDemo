package com.ccc.lib.notification.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.ccc.lib.notification.util.bean.BaseNotificationDataBean;
import com.ccc.lib.notification.util.log.LogUtil;
import com.ccc.lib.notification.util.util.ParcelabelUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date：2018/7/26 10:25
 * <p>
 * author: CodingCodersCode
 * <p>
 * 通知管理类
 */
public class CCCNotificationUtil {
    private String LOG_TAG = getClass().getCanonicalName();

    //单个通知点击action
    public static final String ACTION_NOTIFICATION_GROUP_CHILD_DELETE = "com.ccc.lib.notification.lib.action.notification.delete";
    //单个通知删除action
    public static final String ACTION_NOTIFICATION_GROUP_CHILD_CLICKED = "com.ccc.lib.notification.lib.action.notification.clicked";
    //通知分组点击action
    public static final String ACTION_NOTIFICATION_GROUP_SUMMARY_CLICKED = "com.ccc.lib.notification.lib.action.notification.group.summary.clicked";

    //通知删除request code
    public static final int REQUEST_CODE_NOTIFICATION_GROUP_CHILD_DELETE = 4000;
    //通知点击request code
    public static final int REQUEST_CODE_NOTIFICATION_GROUP_CHILD_CLICKED = 4000;
    //通知分组点击request code
    public static final int REQUEST_CODE_NOTIFICATION_GROUP_CLICKED = 3050;

    //默认单个通知的id
    //public static final int DEFAULT_NOTIFICATION_GROUP_CHILD_ID = 35000;
    //默认通知分组的id
    public static final int DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N = 30000;

    //默认channel id
    public static final String DEFAULT_CHANNEL_ID = "CCCNotificationUtil_Default_Channel_Id";
    //默认channel name
    public static final String DEFAULT_CHANNEL_NAME = "CCCNotificationUtil_Default_Channel_Name";

    //默认channel group id
    public static final String DEFAULT_CHANNEL_GROUP_ID = "CCCNotificationUtil_Default_Channel_Group_Id";
    //默认channel group name
    public static final String DEFAULT_CHANNEL_GROUP_NAME = "CCCNotificationUtil_Default_Channel_Group_Name";

    /**
     * 通知携带数据在Intent中的key，需要利用{@link com.ccc.lib.notification.util.util.ParcelabelUtil#unmarshall}进行解析
     */
    public static final String IK_NOTIFICATION_PARCELABLE_DATA = "IK_NOTIFICATION_PARCELABLE_DATA";

    /**
     * 通知的id在Intent中的key
     */
    public static final String IK_NOTIFICATION_ID = "IK_NOTIFICATION_ID";

    //是否初始化
    private boolean hasInited = false;

    private Context mContext;

    private NotificationManagerCompat mNotificationManagerCompat;

    //通知DeleteIntent的requestCode原子操作
    private AtomicInteger mDeletePendingIntentRequestCodeAtomic;
    //通知ContentIntent的requestCode原子操作
    private AtomicInteger mContentPendingIntentRequestCodeAtomic;
    //通知分组点击的requestCode原子操作
    private AtomicInteger mNotificationGroupSummaryRequestCodeAtomic;

    private CCCNotificationUtil() {

    }

    private static class CCCNotificationUtilHolder {
        public static CCCNotificationUtil instance = new CCCNotificationUtil();
    }

    public static CCCNotificationUtil getInstance(Context context) {
        CCCNotificationUtil cccNotificationUtil = CCCNotificationUtilHolder.instance;
        if (!cccNotificationUtil.hasInited) {
            cccNotificationUtil.init(context);
        }
        return cccNotificationUtil;
    }

    /**
     * 初始化
     *
     * @param context
     */
    protected synchronized void init(Context context) {
        try {
            if (context == null) {
                throw new NullPointerException("context can not be null");
            }

            if (hasInited) {
                return;
            }

            this.hasInited = true;

            this.mContext = context;

            this.mNotificationManagerCompat = NotificationManagerCompat.from(this.mContext);

            this.mDeletePendingIntentRequestCodeAtomic = new AtomicInteger(REQUEST_CODE_NOTIFICATION_GROUP_CHILD_DELETE);
            this.mContentPendingIntentRequestCodeAtomic = new AtomicInteger(REQUEST_CODE_NOTIFICATION_GROUP_CHILD_CLICKED);
            this.mNotificationGroupSummaryRequestCodeAtomic = new AtomicInteger(REQUEST_CODE_NOTIFICATION_GROUP_CLICKED);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                addChannel(this.mContext, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);

                addChannelGroup(this.mContext, DEFAULT_CHANNEL_GROUP_ID, DEFAULT_CHANNEL_GROUP_NAME);

                bindChannelToChannelGroup(this.mContext, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_GROUP_ID);
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "初始化发生异常，详情见异常信息", e);
        }
    }

/////////////////////////////////////////////////////////////////////////////////
//  添加通知渠道
/////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加通知渠道
     *
     * @param context     上下文
     * @param channelId   渠道id
     * @param channelName 渠道名称
     */
    public void addChannel(Context context, String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addChannel(context, channelId, channelName, NotificationManagerCompat.IMPORTANCE_MAX);
        }
    }

    /**
     * 添加通知渠道
     *
     * @param context     上下文
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道优先级
     */
    public void addChannel(Context context, String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addChannel(context, channelId, channelName, "no desciption", importance);
        }
    }

    /**
     * 添加通知渠道
     *
     * @param context     上下文
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param description 渠道描述
     * @param importance  渠道优先级
     */
    public void addChannel(Context context, String channelId, String channelName, String description, int importance) {
        NotificationChannel channel;
        NotificationManager notificationManager;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel(channelId, channelName, importance);
                channel.enableLights(true);
                channel.enableVibration(false);
                channel.setDescription(description);

                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "添加通知渠道发生异常，详情见异常信息", e);
        }
    }

/////////////////////////////////////////////////////////////////////////////////
//  添加通知渠道组
/////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建通知渠道组
     *
     * @param groupId   渠道组id
     * @param groupName 渠道组名称
     */
    public void addChannelGroup(Context context, @NonNull String groupId, @NonNull String groupName) {
        NotificationManager notificationManager;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));
                }
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建通知渠道组发生异常，详情见异常信息");
        }
    }

/////////////////////////////////////////////////////////////////////////////////
//  将通知渠道绑定到通知渠道组
/////////////////////////////////////////////////////////////////////////////////

    /**
     * 将通知渠道绑定到渠道组
     *
     * @param context
     * @param channelId
     * @param channelGroupId
     */
    public void bindChannelToChannelGroup(Context context, String channelId, String channelGroupId) {
        NotificationManager notificationManager;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.getNotificationChannel(channelId).setGroup(channelGroupId);
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "将通知渠道绑定到渠道组发生异常，详情见异常信息", e);
        }
    }

/////////////////////////////////////////////////////////////////////////////////
//  获取通知数量
/////////////////////////////////////////////////////////////////////////////////

    protected int getActiveNotificationCountExceptGroup() {
        int activeCount = 0;
        StatusBarNotification[] activeNotifications;
        String pkgNameInActiveNotification;
        NotificationManager notificationManager;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                //查询当前展示的所有同志的状态列表
                activeNotifications = notificationManager.getActiveNotifications();

                //获取当前通知栏里，
                for (StatusBarNotification itemNotification : activeNotifications) {

                    pkgNameInActiveNotification = itemNotification.getPackageName();

                    if (TextUtils.equals(pkgNameInActiveNotification, BuildConfig.APPLICATION_ID)) {//属于当前应用的通知
                        if ((DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N == itemNotification.getId())) {
                            //是分组
                        } else {
                            activeCount += 1;
                        }
                    }
                }
            } else {
                activeCount = -1;
            }
        } catch (Exception e) {
            activeCount = -1;
            LogUtil.printLog("e", LOG_TAG, "获取通知数量发生异常，详情见异常信息", e);
        }
        return activeCount;
    }

/////////////////////////////////////////////////////////////////////////////////
//  显示通知
/////////////////////////////////////////////////////////////////////////////////

    /**
     * 显示通知
     *
     * @param context
     * @param notificationId
     * @param title
     * @param content
     */
    public void notify(Context context, int notificationId, String title, String content) {
        notify(context, notificationId, title, content, DEFAULT_CHANNEL_ID);
    }

    /**
     * 显示通知
     *
     * @param context
     * @param notificationId
     * @param title
     * @param content
     * @param channel
     */
    public void notify(Context context, int notificationId, String title, String content, String channel) {
        notify(context, notificationId, title, content, null, null, channel);
    }

    /**
     * 显示通知
     *
     * @param context
     * @param notificationId
     * @param title
     * @param content
     * @param notificationDataBean
     * @param channel
     */
    public void notify(Context context, int notificationId, String title, String content, BaseNotificationDataBean notificationDataBean, String channel) {
        notify(context, notificationId, title, content, createDeletePendingIntent(notificationDataBean, notificationId), createContentPendingIntent(notificationDataBean, notificationId), channel);
    }

    /**
     * 显示通知
     *
     * @param context              上下文
     * @param notificationId       通知id
     * @param title                通知标题文本
     * @param content              通知内容文本
     * @param deletePendingIntent  通知删除Intent
     * @param contentPendingIntent 通知点击Intent
     * @param channel              通知渠道
     */
    public void notify(Context context, int notificationId, String title, String content, PendingIntent deletePendingIntent, PendingIntent contentPendingIntent, String channel) {
        try {
            notify(context,
                    notificationId,
                    title,
                    content,
                    deletePendingIntent,
                    contentPendingIntent,
                    channel,
                    Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS,
                    BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher),
                    R.mipmap.ic_launcher,
                    context.getResources().getColor(R.color.color_000000),
                    true,
                    (Build.VERSION.SDK_INT >= 16) ? Notification.PRIORITY_MAX : NotificationCompat.PRIORITY_MAX,
                    DEFAULT_CHANNEL_GROUP_ID,
                    notificationId == DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N);
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "将消息显示在通知栏发生异常，详情见异常信息", e);
        }
    }

    /**
     * 显示通知
     *
     * @param context
     * @param notificationId
     * @param title
     * @param content
     * @param deletePendingIntent
     * @param contentPendingIntent
     * @param channel
     * @param defaults
     * @param largeIcon
     * @param smallIcon
     * @param colorArgb
     * @param autoCancel
     * @param priority
     * @param groupKey
     * @param isGroupSummary
     */
    public void notify(Context context, int notificationId, String title, String content, PendingIntent deletePendingIntent, PendingIntent contentPendingIntent, String channel,
                       int defaults, Bitmap largeIcon, int smallIcon, int colorArgb, boolean autoCancel, int priority,
                       String groupKey, boolean isGroupSummary) {
        Notification notification;
        NotificationManager notificationManager;
        NotificationCompat.Builder notificationCompactBuilder;
        try {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                channel = TextUtils.isEmpty(channel) ? DEFAULT_CHANNEL_ID : channel;

                notification = new Notification.Builder(context, channel)
                        .setDefaults(defaults)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(smallIcon)
                        .setColor(colorArgb)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(autoCancel)//点击通知栏是否被取消
                        .setPriority(priority)
                        .setDeleteIntent(deletePendingIntent)
                        .setContentIntent(contentPendingIntent)
                        .setGroup(groupKey)
                        .setGroupSummary(isGroupSummary)
                        .build();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                channel = TextUtils.isEmpty(channel) ? DEFAULT_CHANNEL_ID : channel;

                notification = new NotificationCompat.Builder(context, channel)
                        .setDefaults(defaults)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(title)
                        .setColor(colorArgb)
                        .setContentText(content)
                        .setAutoCancel(autoCancel)
                        .setPriority(priority)
                        .setDeleteIntent(deletePendingIntent)
                        .setContentIntent(contentPendingIntent)
                        .setGroup(groupKey)
                        .setGroupSummary(isGroupSummary)
                        .build();
            }else {
                notification = new NotificationCompat.Builder(context)
                        .setDefaults(defaults)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(title)
                        .setColor(colorArgb)
                        .setContentText(content)
                        .setAutoCancel(autoCancel)
                        .setPriority(priority)
                        .setDeleteIntent(deletePendingIntent)
                        .setContentIntent(contentPendingIntent)
                        //.setGroup(DEFAULT_CHANNEL_GROUP_ID)
                        //.setGroupSummary(index == 0)
                        .build();
            }
            */
            channel = TextUtils.isEmpty(channel) ? DEFAULT_CHANNEL_ID : channel;

            notificationCompactBuilder = new NotificationCompat.Builder(context, channel)
                    .setDefaults(defaults)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(title)
                    .setColor(colorArgb)
                    .setContentText(content)
                    .setAutoCancel(autoCancel)
                    .setPriority(priority)
                    .setDeleteIntent(deletePendingIntent)
                    .setContentIntent(contentPendingIntent)
                    .setGroup(groupKey);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationCompactBuilder.setGroupSummary(isGroupSummary);
            }

            notification = notificationCompactBuilder.build();


            mNotificationManagerCompat.notify(notificationId, notification);

            //判断是否需要显示分组，是，则显示，否则，取消
            onIfNeedToShowNotificationGroup(notificationId);
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "将消息显示在通知栏发生异常，详情见异常信息", e);
        }
    }

    /**
     * 判断是否需要显示分组，是，则显示，否则，取消
     */
    private void onIfNeedToShowNotificationGroup(int notificationId) {
        int activeNotificationCountExceptGroup = 0;
        try {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) && (notificationId != DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N)) {

                activeNotificationCountExceptGroup = getActiveNotificationCountExceptGroup();

                if (activeNotificationCountExceptGroup >= 2) {
                    //需要显示分组
                    //notify(mApplication, DEFAULT_GROUP_SUMMARY_ID_UP_ANDROID_N, "分组-title", "分组-content", null, null, null);
                    notify(this.mContext,
                            DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N,
                            "",
                            "",
                            null,
                            createGroupSummaryPendingIntent()/*null*/,
                            DEFAULT_CHANNEL_ID,
                            Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS,
                            BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_launcher),
                            R.mipmap.ic_launcher,
                            this.mContext.getResources().getColor(R.color.color_000000),
                            false,
                            Notification.PRIORITY_MAX,
                            DEFAULT_CHANNEL_GROUP_ID,
                            true);
                } else {
                    //不需要显示分组
                    mNotificationManagerCompat.cancel(DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N);
                }
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "控制分组显示或隐藏发生异常，详情见异常信息", e);
        }
    }

    /**
     * 创建单个通知删除广播Intent
     *
     * @param notificationDataBean
     * @return
     */
    private PendingIntent createDeletePendingIntent(BaseNotificationDataBean notificationDataBean, int notificationId) {

        PendingIntent pendingIntent = null;
        Intent deleteIntent;
        Bundle dataBundle;
        try {
            deleteIntent = new Intent(ACTION_NOTIFICATION_GROUP_CHILD_DELETE);

            dataBundle = new Bundle();
            dataBundle.putParcelable(IK_NOTIFICATION_PARCELABLE_DATA, notificationDataBean);
            dataBundle.putInt(IK_NOTIFICATION_ID, notificationId);
            deleteIntent.putExtras(dataBundle);

            deleteIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            pendingIntent = PendingIntent.getBroadcast(this.mContext, this.mDeletePendingIntentRequestCodeAtomic.incrementAndGet(), deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建通知删除PendingIntent发生异常，详情见异常信息", e);
        }
        return pendingIntent;
    }

    /**
     * 创建单个通知点击Intent
     *
     * @param notificationDataBean
     * @return
     */
    private PendingIntent createContentPendingIntent(BaseNotificationDataBean notificationDataBean, int notificationId) {
        PendingIntent pendingIntent = null;
        Intent contentIntent;
        Bundle dataBundle;
        try {
            contentIntent = new Intent(ACTION_NOTIFICATION_GROUP_CHILD_CLICKED);

            dataBundle = new Bundle();
            dataBundle.putByteArray(IK_NOTIFICATION_PARCELABLE_DATA, ParcelabelUtil.marshall(notificationDataBean));
            dataBundle.putInt(IK_NOTIFICATION_ID, notificationId);

            contentIntent.putExtras(dataBundle);
            contentIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            pendingIntent = PendingIntent.getBroadcast(this.mContext, this.mContentPendingIntentRequestCodeAtomic.incrementAndGet(), contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建通知点击PendingIntent发生异常，详情见异常信息", e);
        }
        return pendingIntent;
    }

    /**
     * 创建通知分组点击Intent
     *
     * @return
     */
    private PendingIntent createGroupSummaryPendingIntent() {
        PendingIntent pendingIntent = null;
        Intent summaryClickIntent;
        Bundle dataBundle;
        try {
            summaryClickIntent = new Intent(ACTION_NOTIFICATION_GROUP_SUMMARY_CLICKED);

            dataBundle = new Bundle();
            dataBundle.putInt(IK_NOTIFICATION_ID, DEFAULT_NOTIFICATION_GROUP_ID_UP_ANDROID_N);

            summaryClickIntent.putExtras(dataBundle);

            summaryClickIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            pendingIntent = PendingIntent.getBroadcast(this.mContext, this.mNotificationGroupSummaryRequestCodeAtomic.incrementAndGet(), summaryClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "创建通知分组点击PendingIntent发生异常，详情见异常信息", e);
        }
        return pendingIntent;
    }

    /**
     * 删除所有已显示通知
     */
    public void onCancelAllNotification() {
        try {
            if (this.mNotificationManagerCompat != null) {
                this.mNotificationManagerCompat.cancelAll();
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "删除所有已显示通知发生异常，详情见异常信息", e);
        }
    }

    /**
     * 删除指定通知
     *
     * @param notificationId
     */
    public void onCancelNotification(int notificationId) {
        try {
            if (this.mNotificationManagerCompat != null) {
                this.mNotificationManagerCompat.cancel(notificationId);
            }
        } catch (Exception e) {
            LogUtil.printLog("e", LOG_TAG, "删除指定通知发生异常，详情见异常信息", e);
        }
    }
}
