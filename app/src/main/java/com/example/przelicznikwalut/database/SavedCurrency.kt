package com.example.przelicznikwalut.database

// Klasa danych reprezentująca jedną walutę dodaną przez użytkownika. Pole 'code'
// (np. "USD", "EUR") to klucz główny.

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class SavedCurrency(
    @PrimaryKey val code: String // np. "EUR", "USD"
)
