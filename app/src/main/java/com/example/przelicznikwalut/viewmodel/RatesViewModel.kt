package com.example.przelicznikwalut.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.przelicznikwalut.api.NbpService
import com.example.przelicznikwalut.database.SavedCurrency
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RatesViewModel(application: Application) : AndroidViewModel(application) {

    // Inicjalizacja instancji Firebase Firestore
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    // Referencja do kolekcji z zapisanymi walutami
    private val currenciesCollection by lazy { firestore.collection("currencies") }
    private val _savedCurrencies = MutableStateFlow<List<SavedCurrency>>(emptyList())
    val savedCurrencies: StateFlow<List<SavedCurrency>> = _savedCurrencies.asStateFlow()

    private val _exchangeRates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val exchangeRates: StateFlow<List<CurrencyRate>> = _exchangeRates.asStateFlow()

    init {
        loadCurrencies()
    }

    // Pobiera listę walut z Firestore i kursy z dnia dzisiejszego
    fun loadCurrencies() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        viewModelScope.launch {
            try {
                val snapshot = currenciesCollection.get().await()
                val currencies = snapshot.toObjects(SavedCurrency::class.java)

                _savedCurrencies.value = currencies

                val updatedRates = currencies.map { currency ->
                    if (currency.code == "PLN") {
                        CurrencyRate(currency.code, 1.0, today, today)
                    } else {
                        getRateFor(currency.code, today)
                    }
                }

                _exchangeRates.value = updatedRates
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCurrency(code: String) {
        viewModelScope.launch {
            try {
                val currency = SavedCurrency(code)
                // Zapis dokumentu za pomocą nadania mu ID równego kodowi waluty (np. "EUR")
                currenciesCollection.document(code).set(currency).await()
                loadCurrencies()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeCurrency(currency: SavedCurrency) {
        viewModelScope.launch {
            try {
                // Usunięcie dokumentu po jego unikalnym ID (kodzie waluty)
                currenciesCollection.document(currency.code).delete().await()
                loadCurrencies()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Pobiera listę walut z Firestore i kursy z dnia w argumencie funkcji
    fun loadCurrenciesForDate(date: String) {
        viewModelScope.launch {
            try {
                val snapshot = currenciesCollection.get().await()
                val currencies = snapshot.toObjects(SavedCurrency::class.java)

                _savedCurrencies.value = currencies

                val updatedRates = currencies.map { currency ->
                    if (currency.code == "PLN") {
                        CurrencyRate(currency.code, 1.0, date, date)
                    } else {
                        getRateFor(currency.code, date)
                    }
                }
                _exchangeRates.value = updatedRates
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getRateFor(code: String, requestedDate: String): CurrencyRate {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(requestedDate)!!

        repeat(7) { attempt ->
            val dateStr = sdf.format(calendar.time)
            try {
                val response = NbpService.api.getRateForDate(code, dateStr)
                return CurrencyRate(
                    code = code,
                    rate = response.rates.first().mid,
                    actualDate = response.rates.first().effectiveDate,
                    requestedDate = requestedDate
                )
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                } else {
                    return CurrencyRate(code, 0.0, "błąd", requestedDate)
                }
            } catch (e: Exception) {
                return CurrencyRate(code, 0.0, "błąd", requestedDate)
            }
        }

        return CurrencyRate(code, 0.0, "brak danych", requestedDate)
    }
}

data class CurrencyRate(
    val code: String,
    val rate: Double,
    val actualDate: String,
    val requestedDate: String
)