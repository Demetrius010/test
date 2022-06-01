package com.bignerdranch.android.composetest

import java.net.HttpURLConnection
import java.net.URL
import kotlin.Result

//sealed class Result<out R>{}
//    data class Succes<out T>(val data: T): Result<T>()
//    data class Error(val exception: Exception): Result<Nothing>()
//
//
//class LoginRepository {
//    private val loginUrl = "https://example.com/login"
//    fun makeLoginRequest(jsonBody: String): com.bignerdranch.android.composetest.Result<LoginResponse>{
//        val url = URL(loginUrl)
//        (url.openConnection() as? HttpURLConnection)?.run {
//            requestMethod = "POST"
//            setRequestProperty("Content-Type", "application/json; utf-8")
//            setRequestProperty("Accept", "application/json")
//            doOutput = true
//            outputStream.write(jsonBody.toByteArray())
//            return com.bignerdranch.android.composetest.Result.Succes(responseParser.parse(inputStream))
//        }
//        return  com.bignerdranch.android.composetest.Result.Error(java.lang.Exception("Cannot open HttpURLConnection"))
//    }
//}