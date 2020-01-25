package com.rusmyhal.rates.currencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rusmyhal.rates.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrenciesFragment : Fragment() {

    private val viewModel: CurrenciesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currencies, container, false)
    }

    companion object {
        const val TAG = "CurrenciesFragment"
    }
}