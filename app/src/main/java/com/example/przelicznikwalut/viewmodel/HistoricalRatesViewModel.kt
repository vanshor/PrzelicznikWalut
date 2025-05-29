package com.example.przelicznikwalut.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.przelicznikwalut.api.NbpService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoricalRatesViewModel : ViewModel() {

    private val _history = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val history: StateFlow<List<Pair<String, Double>>> = _history

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun loadHistory(code: String, daysRequested: Int) {
        viewModelScope.launch {
            val merged = mutableListOf<Pair<String, Double>>()
            var remaining = daysRequested
            var endCal = Calendar.getInstance()

            while (remaining > 0) {
                val chunk = minOf(remaining, 255)
                val startCal = (endCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, -chunk + 1)
                }

                try {
                    val resp = NbpService.api.getRatesForPeriod(
                        code,
                        sdf.format(startCal.time),
                        sdf.format(endCal.time)
                    )
                    merged.addAll(0, resp.rates.map { it.effectiveDate to it.mid })
                } catch (e: Exception) {
                    _history.value = emptyList()
                    return@launch
                }

                remaining -= chunk
                endCal = startCal.apply { add(Calendar.DAY_OF_YEAR, -1) }
            }
            _history.value = merged
        }
    }
}

