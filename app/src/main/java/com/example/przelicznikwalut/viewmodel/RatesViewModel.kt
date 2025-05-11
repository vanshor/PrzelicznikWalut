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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RatesViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyDao = AppDatabase.getDatabase(application).currencyDao()

    private val _savedCurrencies = MutableStateFlow<List<SavedCurrency>>(emptyList())
    val savedCurrencies: StateFlow<List<SavedCurrency>> = _savedCurrencies.asStateFlow()
    private val _exchangeRates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val exchangeRates: StateFlow<List<CurrencyRate>> = _exchangeRates.asStateFlow()


    init {
        loadCurrencies()
    }

    // Pobiera listę walut z bazy danych i kursy z dnia dzisiejszego
    fun loadCurrencies() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        viewModelScope.launch {
            val currencies = currencyDao.getAll()
            _savedCurrencies.value = currencies

            val updatedRates = currencies.map { currency ->
                if (currency.code == "PLN") {
                    CurrencyRate(currency.code, 1.0, today, today)
                } else {
                    getRateFor(currency.code, today)
                }
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

    // Pobiera kurs dla danej waluty na określony dzień. Jeśli dla danej daty nie ma
    // kursu (weekend, święta), to pobiera ostatni dostepny kurs w przeciągu ostatnich
    // 7 dni
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
                    calendar.add(Calendar.DAY_OF_MONTH, -1) // Cofnij o 1 dzień i próbuj dalej
                } else {
                    return CurrencyRate(code, 0.0, "błąd", requestedDate)
                }
            } catch (e: Exception) {
                return CurrencyRate(code, 0.0, "błąd", requestedDate)
            }
        }

        // Po 7 próbach brak kursu
        return CurrencyRate(code, 0.0, "brak danych", requestedDate)
    }

    // Pobiera listę walut z bazy danych i kursy z dnia w argumencie funkcji
    fun loadCurrenciesForDate(date: String) {
        viewModelScope.launch {
            val currencies = currencyDao.getAll()
            _savedCurrencies.value = currencies

            val updatedRates = currencies.map { currency ->
                if (currency.code == "PLN") {
                    CurrencyRate(currency.code, 1.0, date, date)
                } else {
                    getRateFor(currency.code, date)
                }
            }
            _exchangeRates.value = updatedRates
        }
    }
}

data class CurrencyRate(
    val code: String,
    val rate: Double,
    val actualDate: String,
    val requestedDate: String
)

