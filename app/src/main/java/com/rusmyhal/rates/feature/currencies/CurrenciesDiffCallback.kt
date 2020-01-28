package com.rusmyhal.rates.feature.currencies

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.rusmyhal.rates.feature.currencies.data.entity.Currency

class CurrenciesDiffCallback : DiffUtil.ItemCallback<Currency>() {

    companion object {
        const val PAYLOAD_CURRENCY_RATE = "payload_currency_rate"
    }

    override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.amount == newItem.amount
    }

    override fun getChangePayload(oldItem: Currency, newItem: Currency): Any? {
        return Bundle().apply {
            putString(PAYLOAD_CURRENCY_RATE, newItem.amount)
        }
    }
}