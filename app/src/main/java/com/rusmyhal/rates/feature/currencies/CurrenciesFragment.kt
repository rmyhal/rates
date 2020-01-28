package com.rusmyhal.rates.feature.currencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rusmyhal.rates.R
import kotlinx.android.synthetic.main.fragment_currencies.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class CurrenciesFragment : Fragment() {

    private val viewModel: CurrenciesViewModel by viewModel()

    private val currenciesAdapter = CurrenciesAdapter({ currency ->
        viewModel.selectCurrency(currency)
    }, { newAmount ->
        viewModel.onAmountChanged(newAmount)
    })

    private var networkErrorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currencies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        viewModel.currencies.observe(viewLifecycleOwner, Observer {
            currenciesAdapter.submitList(it)
        })

        viewModel.networkErrorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                initNetworkErrorSnackbarIfNeeded(errorMessage).let { snackbar ->
                    if (!snackbar.isShown) {
                        snackbar.show()
                    }
                }
            } else {
                networkErrorSnackbar?.dismiss()
                networkErrorSnackbar = null
            }
        })
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        viewModel.startUpdatingCurrencies()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopUpdatingCurrencies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerCurrencies.adapter = null
    }

    private fun setupRecycler() {
        recyclerCurrencies.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerCurrencies.adapter = currenciesAdapter
    }

    private fun initNetworkErrorSnackbarIfNeeded(errorMessage: String): Snackbar {
        if (networkErrorSnackbar == null) {
            networkErrorSnackbar = Snackbar.make(
                recyclerCurrencies,
                errorMessage,
                Snackbar.LENGTH_INDEFINITE
            )
        }
        return networkErrorSnackbar!!
    }
}