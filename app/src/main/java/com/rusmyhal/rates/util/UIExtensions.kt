package com.rusmyhal.rates.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.showKeyboard() {
    val imm: InputMethodManager? = ContextCompat.getSystemService<InputMethodManager>(
        this.context,
        InputMethodManager::class.java
    )
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}