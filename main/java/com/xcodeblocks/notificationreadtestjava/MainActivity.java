package com.xcodeblocks.notificationreadtestjava;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
//import android.support.v4.app.NotificationManagerCompat;
import androidx.core.app.NotificationManagerCompat;

//(아래 리스너 서비스 클래스에서 필요)
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //(다른 앱의 알림을 읽는 권한을 줬는지 확인하고, 안 줬다면 새로 실행한다.)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //(초기 텍스트)
        notificationContent.text = "Nothing yet!" ;

        //(이 앱에 다른 앱 읽는 권한을 주었는가)
        boolean isPermissionAllowed = isNotiPermissionAllowed();
        if(!isPermissionAllowed) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }


// [사용자 정의 함수들 정의 부분]:
// 지금 실행하는 앱인지를 확인하고 권한을 줬었는지
    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

//(리스너 서비스)
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
            }
        }
        @Override
        public void onNotificationRemoved(StatusBarNotification sbn) {
            Log.i("NotificationListener", "[alert] onNotificationRemoved() - " + sbn.toString());
        }
    }
}