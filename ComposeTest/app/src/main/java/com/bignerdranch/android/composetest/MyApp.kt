package com.bignerdranch.android.composetest

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bignerdranch.android.composetest.notifications.MyMessageService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging

class MyApp : Application() {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()//logs a SELECT_CONTENT in your app.
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "myItemId")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "myItemName")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


// Получить токен регистрации для конкретной копипи приложения на устройстве (его можно использовать для отправки уведомлений на конкретное устройство) (В примере используется Postman и в него руками перезаписывают токен)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener{ task->
            if(!task.isSuccessful){
                Log.w("MyApp", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result// Get new FCM registration token
            Log.d("MyApp", "Token: $token")
        })
    }
}