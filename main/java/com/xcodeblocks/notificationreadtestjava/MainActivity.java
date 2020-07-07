package com.xcodeblocks.notificationreadtestjava;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
//import android.support.v4.app.NotificationManagerCompat;
import androidx.core.app.NotificationManagerCompat;
import android.os.Bundle;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //(다른 앱의 알림을 읽는 권한을 줬는지 확인하고, 안 줬다면 새로 실행한다.)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //(이 앱에 다른 앱 읽는 권한을 주었는가)
        boolean isPermissionAllowed = isNotiPermissionAllowed();
        if(!isPermissionAllowed) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }

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
}