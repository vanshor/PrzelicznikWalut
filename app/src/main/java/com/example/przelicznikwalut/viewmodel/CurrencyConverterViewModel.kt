package com.example.przelicznikwalut.viewmodel

// ViewModel odpowiadający za ekran konwersji walut. Przechowuje dane wpisane przez
// użytkownika (waluty, kwota), pobiera kursy z API i oblicza wynik konwersji.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.przelicznikwalut.api.NbpService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.NumberFormat
import java.util.Locale

class CurrencyConverterViewModel : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    // Domyślna waluta źródłowa
    private val _sourceCurrency = MutableStateFlow("EUR")
    val sourceCurrency: StateFlow<String> = _sourceCurrency.asStateFlow()

    // Domyślna waluta docelowa
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
        // Pozwalaj na przecinek w kwocie
        val amountValue = _amount.value.replace(',', '.').toDoubleOrNull()
        val source = _sourceCurrency.value
        val target = _targetCurrency.value

        if (amountValue == null || source.isBlank() || target.isBlank()) {
            _result.value = "Nieprawidłowa kwota lub waluty"
            return
        }

        // Jeśli waluty są takie same – nie przeliczamy
        if (source == target) {
            val numberFormat = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
                isGroupingUsed = true
            }
            _result.value = "${numberFormat.format(amountValue)} $target"
            return
        }

        viewModelScope.launch {
            try {
                suspend fun fetchRate(code: String): Double {
                    return try {
                        NbpService.api.getTodayRate(code).rates.first().mid
                    } catch (e: HttpException) {
                        if (e.code() == 404) {
                            // Pobierz ostatni kurs, gdy dzisiejszy jest niedostępny
                            NbpService.api.getLastRate(code).rates.first().mid
                        } else throw e
                    }
                }

                val resultAmount = when {
                    // Przelicz z PLN na inną walutę → podziel przez kurs
                    source == "PLN" -> amountValue / fetchRate(target)
                    // Przelicz z waluty obcej na PLN → pomnóż przez kurs
                    target == "PLN" -> amountValue * fetchRate(source)
                    else -> {
                        // Inna waluta na inną walutę: source → PLN → target
                        val sourceRate = fetchRate(source)
                        val targetRate = fetchRate(target)
                        (amountValue * sourceRate) / targetRate
                    }
                }

                // Używa polskiej notacji liczbowej (minimalna i makysmalna liczba miejsc po
                // przecinku to 2, oddzielanie tysięcy spacją)
                val numberFormat = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
                    maximumFractionDigits = 2
                    minimumFractionDigits = 2
                    isGroupingUsed = true
                }

                _result.value = "${numberFormat.format(resultAmount)} $target"

            } catch (e: Exception) {
                _result.value = "Błąd pobierania kursu"
            }
        }
    }


    // Zamiana walut
    fun swapCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}
