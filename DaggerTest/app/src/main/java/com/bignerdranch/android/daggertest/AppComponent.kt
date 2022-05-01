package com.bignerdranch.android.daggertest

import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class])
interface AppComponent {
   val computer:Computer// можно и функцией fun computer(): Computer

   fun inject(activity: MainActivity)// функция для отправки зависимости в класс MainActivity, имя функции не имеет значение важна сигнатура
}

