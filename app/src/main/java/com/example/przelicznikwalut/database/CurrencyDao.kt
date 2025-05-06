package com.example.przelicznikwalut.database

// Interfejs DAO dla Room. Zawiera metody do interakcji z bazą danych takie jak
// pobieranie wszystkich zapisanych walut, dodawanie nowej waluty i usuwanie istniejącej
// waluty.

import androidx.room.*

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies")
    suspend fun getAll(): List<SavedCurrency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: SavedCurrency)

    @Delete
    suspend fun delete(currency: SavedCurrency)
}