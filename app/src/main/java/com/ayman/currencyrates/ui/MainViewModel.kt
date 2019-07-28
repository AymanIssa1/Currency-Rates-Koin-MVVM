package com.ayman.currencyrates.ui

import androidx.lifecycle.MutableLiveData
import com.ayman.currencyrates.BaseViewModel
import com.ayman.currencyrates.R
import com.ayman.currencyrates.model.Currency
import com.ayman.currencyrates.remote.RemoteDataSource
import com.ayman.currencyrates.utils.rx.SchedulerProvider
import com.ayman.currencyrates.utils.rx.with
import org.json.JSONException
import org.json.JSONObject

class MainViewModel(private val remoteDataSource: RemoteDataSource, private val scheduler: SchedulerProvider) :
    BaseViewModel() {

    private val ratesLiveData: MutableLiveData<CurrencyRateUIModel> = MutableLiveData()

    fun getRatesLiveData(): MutableLiveData<CurrencyRateUIModel> {
        return ratesLiveData
    }

    fun getRates(currencyType: String) {
        launch {
            remoteDataSource
                .getRates(currencyType)
                .with(scheduler)
                .subscribe({
                    ratesLiveData.postValue(
                        CurrencyRateUIModel(
                            isSuccessful = true,
                            currencyRates = parseRatesJson(it)
                        )
                    )
                }, {
                    ratesLiveData.postValue(CurrencyRateUIModel(errorMessage = it.message))
                })
        }
    }

    fun parseRatesJson(jsonString: String): ArrayList<Currency> {
        val ratesArrayList: ArrayList<Currency> = arrayListOf()
        try {
            val reader = JSONObject(jsonString)
            val base = reader.get("base") as String

            ratesArrayList.add(Currency(base, getCurrencyName(base), 1.0, getCurrencyFlag(base)))

            val rates = reader.getJSONObject("rates")

            val iteratorObj = rates.keys()
            while (iteratorObj.hasNext()) {
                val key = iteratorObj.next() as String // currency perfix
                val value: Double = rates.get(key) as Double // currency value
                ratesArrayList.add(getCurrencyRate(key, value))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ratesArrayList
    }

    fun getCurrencyRate(prefix: String, value: Double): Currency {
        return Currency(prefix, getCurrencyName(prefix), value, getCurrencyFlag(prefix))
    }

    fun getCurrencyName(prefix: String): String {
        return when (prefix) {
            "EUR" -> "Euro"
            "AUD" -> "Australian Dollar"
            "BGN" -> "Bulgarian Lev"
            "BRL" -> "Brazilian Real"
            "CAD" -> "Canadian Dollar"
            "CHF" -> "Swiss Franc"
            "CNY" -> "Renminbi"
            "CZK" -> "Czech Koruna"
            "DKK" -> "Danish Krone"
            "GBP" -> "Pound Sterling"
            "HKD" -> "Hong Kong Dollar"
            "HRK" -> "Croatian Kuna"
            "HUF" -> "Hungarian Forint"
            "IDR" -> "Indonesian Rupiah"
            "ILS" -> "Israeli new Shekel"
            "INR" -> "Indian Rupee"
            "ISK" -> "Icelandic Króna"
            "JPY" -> "Japanese Yen"
            "KRW" -> "South Korean Won"
            "MXN" -> "Mexican peso"
            "MYR" -> "Malaysian Ringgit"
            "NOK" -> "Norwegian Krone"
            "NZD" -> "New Zealand Dollar"
            "PHP" -> "Philippine Peso"
            "PLN" -> "Polish Złoty"
            "RON" -> "Romanian Leu"
            "RUB" -> "Russian Ruble"
            "SEK" -> "Swedish Krona"
            "SGD" -> "Singapore Dollar"
            "THB" -> "Thai Baht"
            "TRY" -> "Turkish Lira"
            "USD" -> "United States Dollar"
            "ZAR" -> "South African Rand"
            else -> ""
        }
    }

    fun getCurrencyFlag(prefix: String): Int {
        return when (prefix) {
            "EUR" -> R.drawable.europe
            "AUD" -> R.drawable.australia
            "BGN" -> R.drawable.bulgaria
            "BRL" -> R.drawable.brazil
            "CAD" -> R.drawable.canada
            "CHF" -> R.drawable.switzerland
            "CNY" -> R.drawable.china
            "CZK" -> R.drawable.czech
            "DKK" -> R.drawable.danish
            "GBP" -> R.drawable.united_kingdom
            "HKD" -> R.drawable.hong_kong
            "HRK" -> R.drawable.croatian
            "HUF" -> R.drawable.hungary
            "IDR" -> R.drawable.indonesia
            "ILS" -> R.drawable.isreal
            "INR" -> R.drawable.india
            "ISK" -> R.drawable.iceland
            "JPY" -> R.drawable.japan
            "KRW" -> R.drawable.south_korea
            "MXN" -> R.drawable.mexican
            "MYR" -> R.drawable.malaysian
            "NOK" -> R.drawable.norwegian
            "NZD" -> R.drawable.new_zealand
            "PHP" -> R.drawable.philippine
            "PLN" -> R.drawable.poland
            "RON" -> R.drawable.romania
            "RUB" -> R.drawable.russia
            "SEK" -> R.drawable.swedish
            "SGD" -> R.drawable.singapore
            "THB" -> R.drawable.thailand
            "TRY" -> R.drawable.turkish
            "USD" -> R.drawable.united_states
            "ZAR" -> R.drawable.south_african
            else -> 0
        }
    }


}

data class CurrencyRateUIModel(
    val isSuccessful: Boolean = false,
    val currencyRates: ArrayList<Currency>? = null,
    val errorMessage: String? = null
)
