package com.ccc.demo.notification.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_show_notification;

    private int index = 0;

    private NotificationMessageManager mNotificationMessageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mNotificationMessageManager = NotificationMessageManager.getInstance(this);

        this.tv_show_notification = (TextView)findViewById(R.id.tv_show_notification);
        this.tv_show_notification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_show_notification:
                showNotification();
                break;
        }
    }

    private void showNotification(){
        TestNotificationBean testBean;
        try {

            testBean = new TestNotificationBean();
            testBean.setTitle("title-" + index);
            testBean.setContent("content-" + index);

            index++;

            this.mNotificationMessageManager.notify(this, testBean.getTitle(), testBean.getContent(), testBean);
        }catch (Exception e){

        }
    }
}
