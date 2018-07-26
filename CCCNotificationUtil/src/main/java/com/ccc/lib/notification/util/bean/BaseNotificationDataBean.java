package com.ccc.lib.notification.util.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date：2018/7/26 11:41
 * <p>
 * author: CodingCodersCode
 */
public class BaseNotificationDataBean implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public BaseNotificationDataBean() {
    }

    protected BaseNotificationDataBean(Parcel in) {
    }

    public static final Parcelable.Creator<BaseNotificationDataBean> CREATOR = new Parcelable.Creator<BaseNotificationDataBean>() {
        @Override
        public BaseNotificationDataBean createFromParcel(Parcel source) {
            return new BaseNotificationDataBean(source);
        }

        @Override
        public BaseNotificationDataBean[] newArray(int size) {
            return new BaseNotificationDataBean[size];
        }
    };
}
