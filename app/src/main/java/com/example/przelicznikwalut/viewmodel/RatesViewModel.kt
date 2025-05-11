package com.example.przelicznikwalut.viewmodel

// ViewModel dla ekranu z kursami walut. Obsługuje pobieranie listy kursów.

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.przelicznikwalut.api.NbpService
import com.example.przelicznikwalut.database.AppDatabase
import com.example.przelicznikwalut.database.SavedCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RatesViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyDao = AppDatabase.getDatabase(application).currencyDao()

    private val _savedCurrencies = MutableStateFlow<List<SavedCurrency>>(emptyList())
    val savedCurrencies: StateFlow<List<SavedCurrency>> = _savedCurrencies.asStateFlow()
    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> = _exchangeRates.asStateFlow()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            val currencies = currencyDao.getAll()
            _savedCurrencies.value = currencies

            val updatedRates = currencies.associate { currency ->
                val rate = try {
                    NbpService.api.getTodayRate(currency.code).rates.first().mid
                } catch (e: HttpException) {
                    if (e.code() == 404)
                        NbpService.api.getLastRate(currency.code).rates.first().mid
                    else 0.0
                } catch (e: Exception) {
                    0.0
                }
                currency.code to rate
            }
            _exchangeRates.value = updatedRates
        }
    }


    fun addCurrency(code: String) {
        viewModelScope.launch {
            currencyDao.insert(SavedCurrency(code))
            loadCurrencies()
        }
    }

    fun removeCurrency(currency: SavedCurrency) {
        viewModelScope.launch {
            currencyDao.delete(currency)
            loadCurrencies()
        }
    }
}
