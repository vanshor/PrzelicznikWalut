package com.example.przelicznikwalut.database

// Klasa danych reprezentująca jedną walutę dodaną przez użytkownika.

data class SavedCurrency(
    val code: String = "" // np. "EUR", "USD"
)
