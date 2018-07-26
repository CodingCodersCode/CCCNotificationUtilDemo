package com.ccc.demo.notification.util;

import android.os.Parcel;

import com.ccc.lib.notification.util.bean.BaseNotificationDataBean;

/**
 * Date：2018/7/26 17:27
 * <p>
 * author: CodingCodersCode
 */
public class TestNotificationBean extends BaseNotificationDataBean {

    private String title;
    private String content;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.content);
    }

    public TestNotificationBean() {
    }

    protected TestNotificationBean(Parcel in) {
        super(in);
        this.title = in.readString();
        this.content = in.readString();
    }

    public static final Creator<TestNotificationBean> CREATOR = new Creator<TestNotificationBean>() {
        @Override
        public TestNotificationBean createFromParcel(Parcel source) {
            return new TestNotificationBean(source);
        }

        @Override
        public TestNotificationBean[] newArray(int size) {
            return new TestNotificationBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
