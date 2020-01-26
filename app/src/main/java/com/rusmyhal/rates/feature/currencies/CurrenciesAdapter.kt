package com.rusmyhal.rates.feature.currencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rusmyhal.rates.R
import com.rusmyhal.rates.feature.currencies.CurrenciesDiffCallback.Companion.PAYLOAD_CURRENCY_RATE
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.util.showKeyboard
import kotlinx.android.synthetic.main.item_currency.view.*


class CurrenciesAdapter(private val clickListener: (currency: Currency) -> Unit) :
    ListAdapter<Currency, CurrenciesAdapter.CurrencyViewHolder>(CurrenciesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_currency,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(PAYLOAD_CURRENCY_RATE)) {
                holder.setRate(bundle.getString(PAYLOAD_CURRENCY_RATE)!!)
            }
        }
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                itemView.inputCurrencyRate.requestFocus()
            }

            itemView.inputCurrencyRate.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    clickListener(getItem(adapterPosition))
                    itemView.inputCurrencyRate.showKeyboard()
                    itemView.inputCurrencyRate.setSelection(
                        itemView.inputCurrencyRate.text.toString().length
                    )
                }
            }

        }

        fun bind(currency: Currency) = with(itemView) {
            imgCountryLogo.setImageResource(currency.flagResId)
            txtCurrencyCode.text = currency.code
            txtCurrencyName.text = currency.displayName
            setRate(currency.amount)
        }

        fun setRate(newRate: String) {
            itemView.inputCurrencyRate.setText(newRate)

            if (itemView.inputCurrencyRate.hasFocus()) {
                itemView.inputCurrencyRate.setSelection(newRate.length)
            }
        }
    }
}