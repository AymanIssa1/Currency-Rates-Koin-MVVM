package com.ayman.currencyrates

import android.app.Application
import com.ayman.currencyrates.di.remoteDataSourceModule
import com.ayman.currencyrates.di.rxModule
import com.ayman.currencyrates.di.viewModelModule
import com.facebook.stetho.Stetho
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this)

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(
                listOf(
                    rxModule,
                    remoteDataSourceModule(),
                    viewModelModule
                )
            )
        }

        val networkInspectorConfiguration = ConnectionBuddyConfiguration.Builder(this).build()
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration)
    }
}