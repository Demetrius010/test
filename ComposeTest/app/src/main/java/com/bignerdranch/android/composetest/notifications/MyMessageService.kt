package com.bignerdranch.android.composetest.notifications


import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//Этот сервис не надо включать через startService(), он сам создается когда приходить сообщение (onCreate, onMessageReceived, onDestroy)
class MyMessageService: FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MyMessageService", "onCreate")
    }

    override fun onNewToken(token: String) {//onNewToken срабатывает всякий раз, когда создается новый токен.
        Log.d("MyMessageService", "Refreshed token: $token")
       // sendRegistrationToServer(token) // созданный токен мы должны отправить нашему серверу чтоб от туда мы могли рассылать сообщения по отдельным токенам  (В примере используется Postman и в него руками перезаписывают токен)
    }

    override fun onMessageReceived(message: RemoteMessage) {// ЭТО СРАБОТАЕТ В СЛУЧАЕ ИСЛИ ПРИЛОЖЕНИЕ ОТКРЫТО В ДАННЫЙ МОМЕНТ (пуш не появляется когда приложение открыто)
        super.onMessageReceived(message)// ЕСЛИ МЫ СВЕРНУЛИ ПРИЛОЖЕНИЕ ТО ПРОСТО ПРИДЕТ ПУШ и сработает onCreate, onDestroy, а этот код не выполнится
        val intent = Intent(MY_INTENT_FILTER)
        Log.d("MyMessageService", "MessageReceived size: ${message.data.size}")
        message.data.forEach{entry ->  //кладем в наш интент все данные из пришедшего пуш уведомления
            Log.d("MyMessageService", "MessageReceived: ${entry.key}, ${entry.value}")
            intent.putExtra(entry.key, entry.value)//эти данные мы указываем в 5 разделе (Additional options (optional)) в консоли
        }
        sendBroadcast(intent)
    }

    companion object {
        const val MY_INTENT_FILTER = "PUSH_EVENT"

        const val KEY_ACTION = "action"
        const val KEY_MESSAGE = "message"
        const val ACTION_SHOW_MESSAGE = "show_message"
    }

    override fun onDestroy() {
        Log.d("MyMessageService", "onDestroy")
        super.onDestroy()
    }
}