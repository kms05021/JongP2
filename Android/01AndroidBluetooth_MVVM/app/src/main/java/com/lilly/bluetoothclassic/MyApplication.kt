package com.lilly.bluetoothclassic

import android.app.Application
import android.content.Context
import com.lilly.bluetoothclassic.di.repositoryModule
import com.lilly.bluetoothclassic.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.logging.Level


class MyApplication : Application() {

    init{
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // 로그를 찍어볼 수 있다.
            // 에러확인 - androidLogger(Level.ERROR)
            androidLogger(org.koin.core.logger.Level.ERROR)
            // Android Content 를 넘겨준다.
            androidContext(this@MyApplication)
            // assets/koin.properties 파일에서 프로퍼티를 가져옴
            androidFileProperties()
            //module list
            modules(listOf(repositoryModule, viewModelModule))
        }

    }

}