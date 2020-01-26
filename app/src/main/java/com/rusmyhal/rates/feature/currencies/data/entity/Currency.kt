package com.rusmyhal.rates.feature.currencies.data.entity

import androidx.annotation.DrawableRes

data class Currency(
    val code: String,
    val displayName: String,
    val rate: String,
    @DrawableRes val flagResId: Int
)