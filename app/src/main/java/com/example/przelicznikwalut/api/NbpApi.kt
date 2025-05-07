package com.example.przelicznikwalut.api

// Interfejs Retrofit, który definiuje zapytania HTTP do API Narodowego Banku Polskiego.
// Umożliwia pobranie aktualnych i historycznych kursów walut.

import retrofit2.http.GET
import retrofit2.http.Path

interface NbpApi {
    @GET("exchangerates/rates/A/{code}/today/?format=json")
    suspend fun getTodayRate(@Path("code") code: String): ExchangeRateResponse
}
