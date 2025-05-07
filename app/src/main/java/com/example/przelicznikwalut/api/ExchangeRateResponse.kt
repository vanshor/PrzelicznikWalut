package com.example.przelicznikwalut.api

data class ExchangeRateResponse(
    val rates: List<Rate>
)

data class Rate(
    val mid: Double
)
