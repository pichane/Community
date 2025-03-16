package com.example.myapp.di

import com.example.myapp.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // Database
    single { AppDatabase.getInstance(androidContext()) }

    // DAOs
    single { get<AppDatabase>().photoDao() }
}