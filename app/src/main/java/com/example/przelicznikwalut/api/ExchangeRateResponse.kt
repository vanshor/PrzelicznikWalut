package com.example.przelicznikwalut.api

// Reprezentuje odpowiedź API NBP dla konkretnego kursu waluty
data class ExchangeRateResponse(
    val rates: List<Rate>
)

// Reprezentuje pojedynczy kurs średni danej waluty oraz datę jego obowiązywania
// (którego dnia został opublikowany przez NBP)
data class Rate(
    val mid: Double,
    val effectiveDate: String
)
