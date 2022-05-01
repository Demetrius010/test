package com.bignerdranch.android.daggertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var computer: Computer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.appComponent.inject(this)//получаем appComponent от нашей функции расширения из MyApplication.kt, инициализиурем инжект в эту активити
        //val computer: Computer = appComponent.computer// но лучше делать через Inject.  А тут т.к. активити наследутся от context то тут сразу доступен appComponent
        print(computer)
    }
}

