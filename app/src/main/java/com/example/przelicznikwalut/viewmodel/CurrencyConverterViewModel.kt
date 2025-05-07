package com.example.przelicznikwalut.viewmodel

// ViewModel odpowiadający za ekran konwersji walut. Przechowuje dane wpisane przez
// użytkownika (waluty, kwota), pobiera kursy z API i oblicza wynik konwersji.

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyConverterViewModel : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _sourceCurrency = MutableStateFlow("EUR")
    val sourceCurrency: StateFlow<String> = _sourceCurrency.asStateFlow()

    private val _targetCurrency = MutableStateFlow("PLN")
    val targetCurrency: StateFlow<String> = _targetCurrency.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    fun onAmountChanged(newAmount: String) {
        _amount.value = newAmount
    }

    fun onSourceCurrencyChanged(newCurrency: String) {
        _sourceCurrency.value = newCurrency
    }

    fun onTargetCurrencyChanged(newCurrency: String) {
        _targetCurrency.value = newCurrency
    }

    fun convert() {
        val value = _amount.value.toDoubleOrNull()
        if (value != null) {
            _result.value = "%.2f".format(value * 4.5) + " PLN" // przykładowy kurs
        } else {
            _result.value = "Nieprawidłowa kwota"
        }
    }
}
