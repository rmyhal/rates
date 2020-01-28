package com.rusmyhal.rates.feature.currencies.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rusmyhal.rates.R
import com.rusmyhal.rates.feature.currencies.ui.CurrenciesDiffCallback.Companion.PAYLOAD_CURRENCY_RATE
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.util.DecimalDigitsInputFilter
import com.rusmyhal.rates.util.showKeyboard
import kotlinx.android.synthetic.main.item_currency.view.*


class CurrenciesAdapter(
    private val clickListener: (currency: Currency) -> Unit,
    private val onAmountChangeListener: (amount: String) -> Unit
) :
    ListAdapter<Currency, CurrenciesAdapter.CurrencyViewHolder>(
        CurrenciesDiffCallback()
    ) {

    companion object {
        private const val RATE_MAX_DIGITS = 2
        private const val RATE_MAX_LENGTH = 10
    }

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

        private lateinit var enterAmountWatcher: TextWatcher

        init {
            with(itemView) {
                setOnClickListener {
                    inputCurrencyRate.requestFocus()
                }
                inputCurrencyRate.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        clickListener(getItem(adapterPosition))
                        inputCurrencyRate.showKeyboard()
                        inputCurrencyRate.setSelection(
                            inputCurrencyRate.text.toString().length
                        )
                        enterAmountWatcher = inputCurrencyRate.doAfterTextChanged { inputAmount ->
                            onAmountChangeListener(inputAmount.toString())
                        }
                    } else {
                        if (::enterAmountWatcher.isInitialized) {
                            inputCurrencyRate.removeTextChangedListener(enterAmountWatcher)
                        }
                    }
                }

                inputCurrencyRate.filters = arrayOf(
                    DecimalDigitsInputFilter(RATE_MAX_DIGITS),
                    InputFilter.LengthFilter(RATE_MAX_LENGTH)
                )
            }
        }

        fun bind(currency: Currency) = with(itemView) {
            imgCountryLogo.setImageResource(currency.flagResId)
            txtCurrencyCode.text = currency.code
            txtCurrencyName.text = currency.displayName
            setRate(currency.amount)
        }

        fun setRate(newRate: String) = with(itemView) {
            if (!inputCurrencyRate.hasFocus()) {
                inputCurrencyRate.setText(newRate)
            }
        }
    }
}