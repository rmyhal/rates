package com.rusmyhal.rates

import com.rusmyhal.rates.feature.currencies.data.entity.CurrenciesResponse
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate

object TestUtil {

    val CURRENCY_RATE_1 = CurrencyRate("USD", 1.1234f)
    val CURRENCY_RATE_2 = CurrencyRate("GBP", 0.7812f)

    val DEFAULT_CURRENCY = Currency("EUR", "1.00", 1)
    val CURRENCY_1 = Currency(CURRENCY_RATE_1.code, "1.12", 1)
    val CURRENCY_2 = Currency(CURRENCY_RATE_2.code, "0.78", 1)

    val DEFAULT_CURRENCY_X10 = Currency("EUR", "10.00", 1)
    val CURRENCY_1_X10 = Currency(CURRENCY_RATE_1.code, "11.23", 1)
    val CURRENCY_2_X10 = Currency(CURRENCY_RATE_2.code, "7.81", 1)

    val DEFAULT_CURRENCY_X0 = Currency("EUR", "", 1)
    val CURRENCY_1_X0 = Currency(CURRENCY_RATE_1.code, "", 1)
    val CURRENCY_2_X0 = Currency(CURRENCY_RATE_2.code, "", 1)

    val CURRENCY_RESPONSE_1 =
        CurrenciesResponse("EUR", mapOf(CURRENCY_RATE_1.code to CURRENCY_RATE_1.rate))

    val CURRENCY_RESPONSE_2 =
        CurrenciesResponse(
            "EUR", mapOf(
                CURRENCY_RATE_1.code to CURRENCY_RATE_1.rate,
                CURRENCY_RATE_2.code to CURRENCY_RATE_2.rate
            )
        )
}