package com.ayman.currencyrates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ayman.currencyrates.di.remoteDataSourceModule
import com.ayman.currencyrates.di.rxModule
import com.ayman.currencyrates.di.viewModelModule
import com.ayman.currencyrates.remote.RemoteDataSource
import com.ayman.currencyrates.ui.CurrencyRateUIModel
import com.ayman.currencyrates.ui.MainViewModel
import com.ayman.currencyrates.utils.TestSchedulerProvider
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainViewModelTest : KoinTest {

    lateinit var mainViewModel: MainViewModel

    @Mock
    lateinit var remoteDataSource: RemoteDataSource
    @Mock
    lateinit var ratesObserver: Observer<CurrencyRateUIModel>

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    rxModule,
                    remoteDataSourceModule(),
                    viewModelModule
                )
            )
        }
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(remoteDataSource, TestSchedulerProvider())
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetCurrentCurrencyName() {
        assertEquals(mainViewModel.getCurrencyName("EUR"), "Euro")
    }

    @Test
    fun testGetCurrencyFlag() {
        assertEquals(mainViewModel.getCurrencyFlag("EUR"), R.drawable.europe)
    }

    @Test
    fun testGetRates() {
        val jsonSample =
            "{\"base\":\"EUR\",\"date\":\"2018-09-06\",\"rates\":{\"AUD\":1.6118,\"BGN\":1.9502,\"BRL\":4.7781,\"CAD\":1.5294,\"CHF\":1.1243,\"CNY\":7.9225,\"CZK\":25.642,\"DKK\":7.4355,\"GBP\":0.89568,\"HKD\":9.1064,\"HRK\":7.4129,\"HUF\":325.56,\"IDR\":17274.0,\"ILS\":4.1587,\"INR\":83.479,\"ISK\":127.44,\"JPY\":129.18,\"KRW\":1301.0,\"MXN\":22.302,\"MYR\":4.7983,\"NOK\":9.7481,\"NZD\":1.7583,\"PHP\":62.414,\"PLN\":4.306,\"RON\":4.6253,\"RUB\":79.348,\"SEK\":10.561,\"SGD\":1.5954,\"THB\":38.021,\"TRY\":7.6065,\"USD\":1.1601,\"ZAR\":17.773}}"

        Mockito.`when`(remoteDataSource.getRates(ArgumentMatchers.anyString()))
            .thenReturn(Single.just(jsonSample))

        mainViewModel.getRatesLiveData().observeForever(ratesObserver)

        mainViewModel.getRates("Euro")

        verify(ratesObserver).onChanged(
            CurrencyRateUIModel(
                isSuccessful = true,
                currencyRates = mainViewModel.parseRatesJson(jsonSample)
            )
        )
    }
}