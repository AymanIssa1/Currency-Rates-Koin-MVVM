package com.ayman.currencyrates.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ayman.currencyrates.R
import com.ayman.currencyrates.model.Currency
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener
import com.zplesac.connectionbuddy.models.ConnectivityEvent
import com.zplesac.connectionbuddy.models.ConnectivityState
import iammert.com.library.Status
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity(), ConnectivityChangeListener {

    private val model: MainViewModel by viewModel()
    private var currencyType = "EUR"
    private val delay: Long = 1000 // 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // get currency rates every 1 second
        Timer().schedule(object : TimerTask() {
            override fun run() {
                model.getRates(currencyType)
            }
        }, 0, delay)

        model.getRatesLiveData().observe(this, Observer {
            if (it.isSuccessful) {
                progress_bar.visibility = View.INVISIBLE
                setCurrencyRatesRecyclerView(it.currencyRates!!)
            } else {
                progress_bar.visibility = View.INVISIBLE
            }
        })
    }

    private fun setCurrencyRatesRecyclerView(currencyRatesList: ArrayList<Currency>) {
        if (currency_rates_recycler_view.adapter == null) {
            currency_rates_recycler_view.adapter = CurrencyRatesAdapter(this, currencyRatesList, onItemClick = {
                currencyType = it
            }, onAmountChangeClick = {
                (currency_rates_recycler_view.adapter as CurrencyRatesAdapter).notifyDatasetChangeExceptFirstItem()
            })
        } else {
            (currency_rates_recycler_view.adapter as CurrencyRatesAdapter).setCurrencyRatesList(currencyRatesList)
            (currency_rates_recycler_view.adapter as CurrencyRatesAdapter).notifyDatasetChangeExceptFirstItem()
        }
    }

    override fun onStart() {
        super.onStart()
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this)
    }

    override fun onStop() {
        super.onStop()
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this)
    }

    override fun onConnectionChange(event: ConnectivityEvent) {
        if (event.state.value == ConnectivityState.CONNECTED) {
            connection_status_view.setStatus(Status.COMPLETE)
        } else if (event.state.value == ConnectivityState.DISCONNECTED) {
            connection_status_view.setStatus(Status.ERROR)
        }
    }


}
