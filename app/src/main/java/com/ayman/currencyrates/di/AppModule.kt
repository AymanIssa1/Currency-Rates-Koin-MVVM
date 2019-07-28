package com.ayman.currencyrates.di

import com.ayman.currencyrates.ui.MainViewModel
import com.ayman.currencyrates.utils.rx.ApplicationSchedulerProvider
import com.ayman.currencyrates.utils.rx.SchedulerProvider
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val rxModule = module {
    factory<SchedulerProvider> { ApplicationSchedulerProvider() }
}

val viewModelModule = module {
    viewModel<MainViewModel>()
}