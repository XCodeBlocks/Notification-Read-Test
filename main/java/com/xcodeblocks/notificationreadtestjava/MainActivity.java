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
import android.widget.TextView;

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

//(리스너 서비스)

}