package com.ayman.currencyrates.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayman.currencyrates.R
import com.ayman.currencyrates.model.Currency
import kotlinx.android.synthetic.main.currency_item.view.*
import java.text.DecimalFormat


class CurrencyRatesAdapter(
    private val context: Context,
    private var currencyRatesList: ArrayList<Currency>,
    private val onItemClick: (currencyType: String) -> Unit,
    private val onAmountChangeClick: () -> Unit
) : RecyclerView.Adapter<CurrencyRatesAdapter.CurrencyRatesViewModel>() {

    private var amount: Double = 1.0
    private var decimalFormat = DecimalFormat("#.##")

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setAmount(if (s!!.isNotEmpty()) (s.toString()).toDouble() else 0.0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRatesViewModel {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return CurrencyRatesViewModel(itemView)
    }

    override fun getItemCount(): Int {
        return currencyRatesList.size
    }

    override fun onBindViewHolder(holder: CurrencyRatesViewModel, position: Int) {
        val item = currencyRatesList[position]
        holder.currencyImageView.setImageDrawable(context.getDrawable(item.imageResource))
        holder.currencyNameTextView.text = item.name
        holder.currencyPrefixTextView.text = item.prefix
        if (position == 0) {
            holder.currencyAmountEditText.setText("$amount")
        } else {
            holder.currencyAmountEditText.setText(decimalFormat.format(amount * item.value))
        }

        holder.currencyAmountEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                amount = holder.currencyAmountEditText.text.toString().toDouble()
                onItemClick.invoke(holder.currencyPrefixTextView.text.toString())
                holder.moveUp()
                holder.currencyAmountEditText.requestFocus()
                holder.currencyAmountEditText.addTextChangedListener(textWatcher)
            } else {
                holder.currencyAmountEditText.removeTextChangedListener(textWatcher)
            }
        }

        holder.itemView.setOnClickListener {
            holder.currencyAmountEditText.requestFocus()
        }
    }

    fun setCurrencyRatesList(currencyRatesList: ArrayList<Currency>) {
        this.currencyRatesList = currencyRatesList
    }

    fun setAmount(amount: Double) {
        this.amount = amount
        onAmountChangeClick.invoke()
    }

    fun notifyDatasetChangeExceptFirstItem() {
        for (i in 1 until itemCount)
            notifyItemChanged(i, currencyRatesList[i])
    }

    inner class CurrencyRatesViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyImageView: ImageView = itemView.currency_image_view
        val currencyPrefixTextView: TextView = itemView.currency_prefix_text_view
        val currencyNameTextView: TextView = itemView.currency_name_text_view
        val currencyAmountEditText: EditText = itemView.currency_amount_edit_text

        fun moveUp() {
            layoutPosition.takeIf { it > 0 }?.also { currentPosition ->
                currencyRatesList.removeAt(currentPosition).also {
                    currencyRatesList.add(0, it)
                }
                notifyItemMoved(currentPosition, 0)
            }
        }
    }
}