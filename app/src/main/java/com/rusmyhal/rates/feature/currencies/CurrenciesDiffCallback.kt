package com.rusmyhal.rates.feature.currencies

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.rusmyhal.rates.feature.currencies.data.entity.Currency

class CurrenciesDiffCallback(
    private val oldCurrencies: List<Currency>,
    private val newCurrencies: List<Currency>
) : DiffUtil.Callback() {

    companion object {
        const val PAYLOAD_CURRENCY_RATE = "payload_currency_rate"
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldCurrencies[oldItemPosition].code == newCurrencies[newItemPosition].code

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrencies[oldItemPosition] == newCurrencies[newItemPosition]
    }

    override fun getOldListSize() = oldCurrencies.size

    override fun getNewListSize() = newCurrencies.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return if (oldCurrencies[oldItemPosition].rate != newCurrencies[newItemPosition].rate) {
            Bundle().apply {
                putString(PAYLOAD_CURRENCY_RATE, newCurrencies[newItemPosition].rate)
            }
        } else {
            null
        }
    }
}