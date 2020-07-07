package com.xcodeblocks.notificationreadtestjava;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
//import android.support.v4.app.NotificationManagerCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;
import android.widget.TextView;

//(아래 리스너 서비스 클래스에서 필요)
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.IntentFilter;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //(다른 앱의 알림을 읽는 권한을 줬는지 확인하고, 안 줬다면 새로 실행한다.)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //(초기 텍스트)
        TextView notificationContent = (TextView)findViewById(R.id.notificationContent);
        notificationContent.setText("Nothing yet!!");

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

//(브로드캐스트 받는 리시버 관련)

    //(인텐트(intent) 수신 관련)
    @Override
    protected void onResume() {
        super.onResume();

        //cf: https://goodtogreate.tistory.com/entry/Activity와-Service간의-통신
        // action 이름이 "custom-event-name"으로 정의된 intent를 수신한다.
        // observer의 이름은 mMessageReceiver이다.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 등록을 해제한다.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
    }

    // 수신된 인텐트를 처리하는 핸들러
    // "custom-event-name"라는 이름의 액션이 브로드캐스트 되면 이게 호출된다.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Intent에 있는 추가 정보를 가져온다.
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

            //(TextView에 실제 텍스트로 출력)
            TextView notificationContent = (TextView)findViewById(R.id.notificationContent);
            notificationContent.setText("Got message: " + message);
        }
    };

}