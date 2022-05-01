package com.bignerdranch.android.daggertest

import android.app.Application
import android.content.Context

final class MyApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }
}
// чтобы получить appComponent можно кастить applicationContext в клоссах приложения
// А можно создать функцию расширения для типа Context, назавем его appComponent
val Context.appComponent: AppComponent
    get() = when(this){//проверяем какого типа контекст на котором вызвали наше расширение
        is MyApplication -> appComponent//если это наше MyApplication то сразу отдаем нах appComponent
        else -> this.applicationContext.appComponent// иначи из полученного контекста получаем MyApplication и извлекаем наш appComponent
    }// в else получается рекурсивный вызов
