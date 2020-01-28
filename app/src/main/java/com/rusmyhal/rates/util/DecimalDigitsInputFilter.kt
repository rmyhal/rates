package com.rusmyhal.rates.util

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter(private val maxDecimalDigits: Int) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var dotPosition = -1
        val inputLength: Int = dest.length

        for (i in 0 until inputLength) {
            val c: Char = dest.toString()[i]
            if (c == '.' || c == ',') {
                dotPosition = i
                break
            }
        }

        if (dotPosition >= 0) { // protects against many dots
            if (source == "." || source == ",") {
                return ""
            }
            // if the text is entered before the dot
            if (dend <= dotPosition) {
                return null
            }
            if (inputLength - dotPosition > maxDecimalDigits) {
                return ""
            }
        } else {
            if (source == "." || source == ",") {
                if (inputLength - dend > maxDecimalDigits) {
                    return ""
                }
            }
        }
        return null
    }
}