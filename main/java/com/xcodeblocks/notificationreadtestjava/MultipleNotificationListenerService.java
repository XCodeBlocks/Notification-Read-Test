package com.xcodeblocks.notificationreadtestjava;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MultipleNotificationListenerService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotificationListener", "[alert] onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NotificationListener", "[alert] onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("NotificationListener", "[alert] onDestroy()");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        final String packageName = sbn.getPackageName();
        //(sbn으로 오는 알림을 가지고 처리)
        //(확인한 알림이 원하는 패키지일때: 여기서는 카카오톡 앱 (또는 구글 메세지 앱) 또는 pushbullet 앱)
        if ( !TextUtils.isEmpty(packageName) &&
                ( packageName.equals("com.kakao.talk") || packageName.equals("com.google.android.apps.messaging") || packageName.equals("com.pushbullet.android") ) )
        {
            Log.i("NotificationListener", "[alert] onNotificationPosted() - " + sbn.toString());
            Log.i("NotificationListener", "[alert] PackageName:" + sbn.getPackageName());
            Log.i("NotificationListener", "[alert] PostTime:" + sbn.getPostTime());

            Notification notificatin = sbn.getNotification();
            Bundle extras = notificatin.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            int smallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap largeIcon = ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

            Log.i("NotificationListener", "[alert] Title:" + title);
            Log.i("NotificationListener", "[alert] Text:" + text);
            Log.i("NotificationListener", "[alert] Sub Text:" + subText);

            //(실제로 화면에 출력하기 위해 textView로 내용 출력):
/*
            TextView notificationContent = findViewById(R.id.notificationContent);
            notificationContent.setText( "[alert] PackageName:" + sbn.getPackageName() + "\n"
                    + "[alert] PostTime:" + sbn.getPostTime() + "\n"
                    + "[alert] Title:" + title + "\n"
                    + "[alert] Text:" + text + "\n"
                    + "[alert] Sub Text:" + subText );
*/
        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationListener", "[alert] onNotificationRemoved() - " + sbn.toString());
    }


    //"custom-event-name"라고 된 이름의 액션을 포함하는 인텐트를 보낸다.
    //-> 메인 액티비티 쪽에서 받게 된다.
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}