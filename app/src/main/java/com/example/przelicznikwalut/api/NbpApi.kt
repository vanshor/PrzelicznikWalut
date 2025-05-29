package com.example.przelicznikwalut.api

// Interfejs Retrofit, który definiuje zapytania HTTP do API Narodowego Banku Polskiego.
// Umożliwia pobranie aktualnych i historycznych kursów walut.

import retrofit2.http.GET
import retrofit2.http.Path

interface NbpApi {
    // Kurs dzisiejszy
    @GET("exchangerates/rates/A/{code}/today/?format=json")
    suspend fun getTodayRate(@Path("code") code: String): ExchangeRateResponse

    // Kurs ostatni (w przypadku, gdy dzisiejszy nie jest dostępny)
    @GET("exchangerates/rates/A/{code}/last/1/?format=json")
    suspend fun getLastRate(@Path("code") code: String): ExchangeRateResponse

    // Kurs na określony dzień
    @GET("exchangerates/rates/A/{code}/{date}/?format=json")
    suspend fun getRateForDate(
        @Path("code") code: String,
        @Path("date") date: String
    ): ExchangeRateResponse

    @GET("exchangerates/rates/A/{code}/last/{count}/?format=json")
    suspend fun getLastRates(
        @Path("code") code: String,
        @Path("count") count: Int
    ): ExchangeRateResponse

    @GET("exchangerates/rates/A/{code}/{startDate}/{endDate}/?format=json")
    suspend fun getRatesForPeriod(
        @Path("code") code: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ): ExchangeRateResponse
}
