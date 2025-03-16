package com.example.myapp.di


import android.R.attr.level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
// import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
       // val loggingInterceptor = HttpLoggingInterceptor().apply {
       //     level = HttpLoggingInterceptor.Level.BODY
       // }

        OkHttpClient.Builder()
           // .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Replace with your API base URL
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}