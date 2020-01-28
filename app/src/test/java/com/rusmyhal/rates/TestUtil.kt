package com.rusmyhal.rates

import com.rusmyhal.rates.feature.currencies.data.entity.CurrenciesResponse
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate

object TestUtil {

    val CURRENCY_RATE_1 = CurrencyRate("USD", 1.1234f)
    val CURRENCY_RATE_2 = CurrencyRate("GBP", 0.7812f)

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