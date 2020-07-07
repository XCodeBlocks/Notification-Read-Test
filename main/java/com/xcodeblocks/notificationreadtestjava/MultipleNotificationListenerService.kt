package com.xcodeblocks.notificationreadtestjava

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MultipleNotificationListenerService : NotificationListenerService() {
    override fun onCreate() {
        super.onCreate()
        Log.i("NotificationListener", "[alert] onCreate()")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("NotificationListener", "[alert] onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("NotificationListener", "[alert] onDestroy()")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        //(sbn으로 오는 알림을 가지고 처리)
        //(확인한 알림이 원하는 패키지일때: 여기서는 카카오톡 앱 (또는 구글 메세지 앱) 또는 pushbullet 앱)
        if (!TextUtils.isEmpty(packageName) &&
                (packageName == "com.kakao.talk" || packageName == "com.google.android.apps.messaging" || packageName == "com.pushbullet.android")) {
            Log.i("NotificationListener", "[alert] onNotificationPosted() - $sbn")
            Log.i("NotificationListener", "[alert] PackageName:" + sbn.packageName)
            Log.i("NotificationListener", "[alert] PostTime:" + sbn.postTime)
            val notificatin = sbn.notification
            val extras = notificatin.extras
            val title = extras.getString(Notification.EXTRA_TITLE)
            val smallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON)
            val largeIcon = extras.getParcelable<Parcelable>(Notification.EXTRA_LARGE_ICON) as Bitmap?
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)
            val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
            Log.i("NotificationListener", "[alert] Title:$title")
            Log.i("NotificationListener", "[alert] Text:$text")
            Log.i("NotificationListener", "[alert] Sub Text:$subText")
            sendMessage(title, text, subText) //(브로드캐스트 보내기)

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

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i("NotificationListener", "[alert] onNotificationRemoved() - $sbn")
    }

    //"custom-event-name"라고 된 이름의 액션을 포함하는 인텐트를 보낸다.
    //-> 메인 액티비티 쪽에서 받게 된다.
    private fun sendMessage(title: String?, text: CharSequence?, subtext: CharSequence?) {
        Log.d("sender", "Broadcasting message")
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "alert received")
        intent.putExtra("title", title)
        intent.putExtra("content", text)
        intent.putExtra("subcontent", subtext)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}