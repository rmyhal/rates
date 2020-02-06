package com.rusmyhal.rates.feature.currencies.data.entity

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CurrenciesResponse(
    @SerializedName("base") val baseCurrency: String,
    @SerializedName("rates") val currenciesRates: Map<String, BigDecimal>
)