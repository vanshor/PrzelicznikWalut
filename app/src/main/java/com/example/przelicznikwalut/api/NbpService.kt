package com.example.przelicznikwalut.api

// Obiekt, który tworzy klienta Retrofit do połączenia z API NBP.
// Używany przez ViewModel do wykonywania zapytań sieciowych.

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NbpService {
    val api: NbpApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nbp.pl/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NbpApi::class.java)
    }
}
