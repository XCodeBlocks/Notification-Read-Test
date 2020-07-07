package com.xcodeblocks.notificationreadtestjava

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//import android.support.v7.app.AppCompatActivity;
//import android.support.v4.app.NotificationManagerCompat;
//(아래 리스너 서비스 클래스에서 필요)
class MainActivity : AppCompatActivity() {
    //(다른 앱의 알림을 읽는 권한을 줬는지 확인하고, 안 줬다면 새로 실행한다.)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //(초기 텍스트)
        val notificationContent = findViewById<View>(R.id.notificationContent) as TextView
        notificationContent.text = "Nothing yet!!"

        //(이 앱에 다른 앱 읽는 권한을 주었는가)
        val isPermissionAllowed = isNotiPermissionAllowed
        if (!isPermissionAllowed) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }

    // [사용자 정의 함수들 정의 부분]:
    // 지금 실행하는 앱인지를 확인하고 권한을 줬었는지
    private val isNotiPermissionAllowed: Boolean
        private get() {
            val notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this)
            val myPackageName = packageName
            for (packageName in notiListenerSet) {
                if (packageName == null) {
                    continue
                }
                if (packageName == myPackageName) {
                    return true
                }
            }
            return false
        }

    //(브로드캐스트 받는 리시버 관련)
    //(인텐트(intent) 수신 관련)
    override fun onResume() {
        super.onResume()

        //cf: https://goodtogreate.tistory.com/entry/Activity와-Service간의-통신
        // action 이름이 "custom-event-name"으로 정의된 intent를 수신한다.
        // observer의 이름은 mMessageReceiver이다.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, IntentFilter("custom-event-name"))
    }

    override fun onPause() {
        super.onPause()
        // 등록을 해제한다.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver)
    }

    // 수신된 인텐트를 처리하는 핸들러
    // "custom-event-name"라는 이름의 액션이 브로드캐스트 되면 이게 호출된다.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        // Intent에 있는 추가 정보를 가져온다.
            val message = intent.getStringExtra("message")
            Log.d("receiver", "<Got message>: $message")
            val title = intent.getStringExtra("title")
            var content = intent.getStringExtra("content")
        //(conent에 아무 것도 없으면 메세지 분석이 의미가 없으니 null 일때는 그냥 더 이상 진행 X)->(임의로 빈 String 넣어서 계속 진행)
            if (content == null) {content = ""}
            Log.d("alert_content", "<alert content>: $content")
            val subcontent = intent.getStringExtra("subcontent")

        //[코틀린에서 원하는 부분 찾기]-(자바에서는 안 되는 방식!)
            //var regex_carrier = "CJ대한통운".toRegex()        //(어떤 택배사가 포함되었는가?)
            var regex_carrier = Regex("CJ대한통운|우체국")     //(cf: 위와 같은 결과)
            //[가져온 알림 내용에서 찾기] -- (여러개 가져올 가능성이 있기에( .next() ) val 대신 var)
            var matchResult : MatchResult? = regex_carrier.find( content )    //(TODO: 여기서 type mismatch(타입 불일치) 뜨는 이유? 특정 상황에서 오류와도 관련?)
        //(cf: https://medium.com/@limgyumin/코틀린-에서-정규-표현식-사용하기-2c655ba35c36 ~~ [4-1] 부분 : 매칭 결과값을 실제 String 값으로 반환하게 함)
            var matched_carrier : String? = matchResult?.value           //(TODO: 굳이 var 써야할까 val 써도될까? -- 나중에 운송장번호 regex로 분리 처리할때는?)

        //(cf: https://www.geeksforgeeks.org/kotlin-regular-expression/ ~~ 특히 containsMatchIn() 부분)
            //val matchDEBUG = regex_carrier.matches(content)            //(비교 대상 '전체'가 regex와 완전히 같아야 true. 지금은 일부만 매칭되면 true여야하므로 적절하지 않은 함수.)
            val hasMatch_carrier = regex_carrier.containsMatchIn(content)      //(비교 대상 내에서 regex와 같은 부분이 하나라도 있으면 true.)
            //Log.d("match?", "$regex_carrier.matches(content).toString()" )    //(로그로 보낼때는 딱 변수만 넣어야 되는 듯)-->(필요 없음)
            Log.d("match?", "$hasMatch_carrier" )
            Log.d("matched_content", "<match result> : $matched_carrier")

        //[찾은(매치) 내용을 화면에 출력]--(현재 화면 하단)
/*
            val regexDebug = findViewById<View>(R.id.regexDEBUG) as TextView
            regexDebug.text = """
                MATCH?: $hasMatch_carrier
                HIT: $matched_carrier
            """.trimIndent()
*/
            //var regex_carrier = """\([A-Z]\w+\)""".toRegex()

        //[TextView에 실제 텍스트로 전체 알림 출력]--(현재 화면 상단)
/*
            val notificationContent = findViewById<View>(R.id.notificationContent) as TextView
            notificationContent.text = """
                Alert Received:
                DEBUG: $message
                title: $title
                content: $content
                subcontent: $subcontent
                """.trimIndent()
*/
            
            val notificationContent = findViewById<View>(R.id.notificationContent) as TextView
            
            if ( !hasMatch_carrier )                            //(매칭되는 배송사 없으면 내용 지우기)
                notificationContent.text = """no match!"""
            else {                                              //(알림에서 배송사가 감지되면)
            //(전체 알림 내용 출력: DEBUG)
                notificationContent.text = """
                Alert Received:
                DEBUG: $message
                title: $title
                content: $content
                subcontent: $subcontent
                """.trimIndent()

            //[운송장번호 찾아서 처리]
                var regex_number = Regex("")    //(아무것도 지정하지 않는다)--(TODO: 변수 타입 몰라서 일단 이렇게 했음)
                var matched_number : String? = ""   //(아무것도 감지되지 않으면 빈 문구)
                if (matched_carrier == "CJ대한통운")    //(CJ대한통운인 경우)
                {
                    regex_number = Regex("[0-9]{4}_[0-9]{4}_[0-9]{4}")
                //[운송장번호 찾기]
                    var matchResult_Number : MatchResult? = regex_number.find(content)
                    //matched_number = matchResult_Number?.value?.replaceAll("_", "")    //(TODO: replaceAll() 함수 사용 불가?!)
                    matched_number = matchResult_Number?.value?.replace("_", "")?.replace("_", "")

                }
                else if (matched_carrier == "우체국")    //(CJ대한통운인 경우)
                {
                    regex_number = Regex("[0-9]{4}_[0-9]{4}_[0-9]{5}")
                    //[운송장번호 찾기]
                    var matchResult_Number : MatchResult? = regex_number.find(content)
                    matched_number = matchResult_Number?.value?.replace("_", "")?.replace("_", "")
                }

            //[정보 출력]
                notificationContent.text = """
                    Carrier Information:
                    배송사: $matched_carrier
                    운송장번호: $matched_number
                """.trimIndent()
            }

        }
    }
}