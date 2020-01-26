package com.rusmyhal.rates.feature.currencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rusmyhal.rates.R
import com.rusmyhal.rates.feature.currencies.CurrenciesDiffCallback.Companion.PAYLOAD_CURRENCY_RATE
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import kotlinx.android.synthetic.main.item_currency.view.*

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    private val currencies: ArrayList<Currency> = arrayListOf()

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
        holder.bind(currencies[position])
    }

    override fun getItemCount() = currencies.size

    fun updateCurrencies(newCurrencies: List<Currency>) {
        val diffResult = DiffUtil.calculateDiff(CurrenciesDiffCallback(currencies, newCurrencies))
        diffResult.dispatchUpdatesTo(this)
        currencies.clear()
        currencies.addAll(newCurrencies)
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(currency: Currency) = with(itemView) {
            imgCountryLogo.setImageResource(currency.flagResId)
            txtCurrencyCode.text = currency.code
            txtCurrencyName.text = currency.displayName
            setRate(currency.rate)
        }

        fun setRate(newRate: String) {
            itemView.inputCurrencyRate.setText(newRate)
        }
    }
}