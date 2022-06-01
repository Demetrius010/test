package com.bignerdranch.android.composetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bignerdranch.android.composetest.notifications.MyMessageService
import com.bignerdranch.android.composetest.ui.theme.ComposeTestTheme//представляет функционал из каталога ui.theme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {//ComponentActivity обееспечивает построение интерфейса из визуальных компонентов и для этого предоставляет минимальный функционал. В частности, ComponentActivity предоставляет метод onCreate(), который вызывается при запуске приложения и создает интерфейс этого приложения.
    private lateinit var pushBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {//В этот метод передается объект @Composable. Этот метод собственно и определяет, что мы увидим на экране устройства.
            ComposeTestTheme {//функция ComposeTestTheme, которая определена в проекте в файле Theme.kt
                // A surface container using the 'background' color from the theme
                Surface(//Surface фактически представляет промежуточный компонент, который задает дополнительное оформление в стиле Material Design.
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")//для вывода простой надписи "Hello Android" используется целый набор компонентов @Composable, которые вложены в друг друга по принципу матрешки: Text -> Greeting -> Surface -> MaterialTheme -> ComposeTestTheme
                }
            }
        }


// Создаем BroadcastReceiver с целью принять данные из Интента созданного в методе onMessageReceived (Этот интент содержит данные полученные в уведомлении)
        pushBroadcastReceiver = object: BroadcastReceiver(){// ЭТО СРАБОТАЕТ В СЛУЧАЕ ИСЛИ ПРИЛОЖЕНИЕ ОТКРЫТО В ДАННЫЙ МОМЕНТ (пуш не появляется когда приложение открыто)
            override fun onReceive(p0: Context?, p1: Intent?) {// ЕСЛИ МЫ СВЕРНУЛИ ПРИЛОЖЕНИЕ ТО ПРОСТО ПРИДЕТ ПУШ, а этот код не выполнится
                Log.d("MainActivity", "onReceive")
                val extras = p1?.extras
                var msgData: String = ""

                extras?.keySet()?.forEach{key->
                    msgData += "$key " + "${extras.get(key)}\n"
                }
                Toast.makeText(this@MainActivity, msgData, Toast.LENGTH_LONG).show()


            // Это можно использовать c Postman как в примере
//                extras?.keySet()?.firstOrNull{it==MyMessageService.KEY_ACTION}?.let{key->
//                    when(extras.getString(key)){
//                        MyMessageService.ACTION_SHOW_MESSAGE ->{
//                            extras.getString(MyMessageService.KEY_MESSAGE)?.let { message ->
//                                Log.d("MainActivity", "Message key-> $message")
//                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        else -> Log.d("MainActivity", "No needed key found")
//                    }
//                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(MyMessageService.MY_INTENT_FILTER)
        registerReceiver(pushBroadcastReceiver, intentFilter)




        //Access a Cloud Firestore
        val db = Firebase.firestore

        /*  ADD DATA  */
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815)// Create a new user with a first and last name
//
//        db.collection("users").add(user).addOnSuccessListener { documentReference ->//// Add a new document with a generated ID
//            Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }.addOnFailureListener{ e->
//                Log.w("MainActivity", "Error adding document", e)
//            }
//
//        val userWithDifferentValues = hashMapOf(//Notice that this document includes a key-value pair (middle name) that does not appear in the first document.
//            "first" to "Alan",
//            "middle" to "Mathison",
//            "last" to "Turing",
//            "born" to 1912
//        )
//        db.collection("users").add(userWithDifferentValues).addOnSuccessListener { documentReference ->//// Add a new document with a generated ID
//            Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }.addOnFailureListener{ e->
//                Log.w("MainActivity", "Error adding document", e)
//            }

        /*   READ DATA   */
        db.collection("users").get().addOnSuccessListener { result ->
            for(document in result){
                Log.d("MainActivity", "${document.id} => ${document.data}")
            }
            }.addOnFailureListener{ e ->
                Log.w("MainActivity", "Error adding document", e)
        }

        /*  Простые запросы */
        val query = db.collection("users").whereEqualTo("first", "Alan")//.whereEqualTo(... //Вы можете объединить несколько методов ля создания более конкретных запросов (логическое AND )
        query.get().addOnSuccessListener { documents ->//Выполнить запрос
            for(document in documents){
                Log.d("MainActivity",  "QUERY RESULT: ${document.id} => ${document.data}")

            }
        }.addOnFailureListener{ e ->
            Log.w("MainActivity", "Error getting document", e)
        }


        /*  получаем обновления в реальном времени   */
        val docRef = db.collection("users").document("myDocumentID")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("MainActivity", "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"
            if (snapshot != null && snapshot.exists()) {
                Log.d("MainActivity", "$source data: ${snapshot.data}")
            } else {
                Log.d("MainActivity", "$source data: null")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pushBroadcastReceiver)
    }
}

@Composable//Объект или функция @Composable представляет некотоый визуальный компонент
fun Greeting(name: String) {// В Compose, composable функция выполняет роль конструктора, отсюда и ее имя с большой буквы, т.к, по факту, это эклектика функции и конструктора
    val count = remember{ mutableStateOf(0)}//объект типа MutableState<Int>    В функцию mutableStateOf передается собственно хранимое значение, которое затем можно получить с помощью свойства value объекта MutableState<T>. А функция remember позволяет сохранить это значение.
    //Для сохранения некоторого объекта в памяти применяется функция remember. Она может хранить как изменяемые (mutable), так и неизменяемые (immutable) объекты. Причем данные объекты сохраняются во время начального построения интерфейса (то что называется initial composition) и продолжают храняться во время обновлений интерфейса (recomposition).
//В сочетании с функцией mutableStateOf есть три способа определения состояния:
//    val mutableState = remember { mutableStateOf(значение) }
//    var value by remember { mutableStateOf(значение) }//применятся делегат by, для работы с которым необходимо импортировать следующие функции:import androidx.compose.runtime.getValue    import androidx.compose.runtime.setValue
//    val (value, setValue) = remember { mutableStateOf(значение) }// возвращается собственно отслеживаемое значение (value) и функция обработки изменения этого значения (setValue)

    Text(text = "Hello $name!\nClicks: ${count.value}",//встроенный компонент - Text, который представляет некоторый текст. Именно этот текст в итоге мы увидим на экране устройства.
         style = TextStyle(fontSize = 22.sp),
         modifier = Modifier.wrapContentSize().background(Color.LightGray).padding(8.dp)//ПОРЯДОК В КОТОРОМ ИДЕТ background ИМЕЕТ ЗНАЧЕНИЕ
             .clickable ( onClick = {count.value += 1})//параметру onClick передается обаботчик нажатия, который изменяет отслеживаемое значение. То есть мы ожидаем, что по нажатию на текст он изменится Однако в случае val count = mutableStateOf(0) если мы запустим проект, то увидим, что понажатию ничего не меняется. Чего-то не хватает - а именно функции - remember
    )
}

@Preview(showBackground = true)//Она указывает, что данный компонент будет применяться для предварительного просмотра.
@Composable
fun DefaultPreview() {
    ComposeTestTheme {//фактически весь тот же самый интерфейс, что и определен в классе MainActivity
        Greeting("Android")
    }
}