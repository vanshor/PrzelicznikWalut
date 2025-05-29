package com.example.przelicznikwalut.ui

// Drugi ekran aplikacji. Wyświetla aktualne albo historyczne kursy wybranych walut dodanych
// przez użytkownika do bazy danych.

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.przelicznikwalut.viewmodel.RatesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RatesScreen(navController: NavHostController, viewModel: RatesViewModel = viewModel()) {
    val savedCurrencies by viewModel.savedCurrencies.collectAsState()
    val allCurrencies = listOf(
        "THB", "USD", "AUD", "HKD", "CAD", "NZD", "SGD", "EUR", "HUF", "CHF", "GBP", "UAH",
        "JPY", "CZK", "DKK", "ISK", "NOK", "SEK", "RON", "BGN", "TRY", "ILS", "CLP", "PHP",
        "MXN", "ZAR", "BRL", "MYR", "IDR", "INR", "KRW", "CNY", "XDR", "PLN"
    )

    var selectedCurrency by remember { mutableStateOf(allCurrencies.first()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val exchangeRates by viewModel.exchangeRates.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ulubione waluty",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("${getFlagForCurrency(selectedCurrency)} $selectedCurrency")
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    allCurrencies.forEach {
                        DropdownMenuItem(
                            text = { Text("${getFlagForCurrency(it)} $it") },
                            onClick = {
                                selectedCurrency = it
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.addCurrency(selectedCurrency) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Dodaj")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Wybór daty
        val context = LocalContext.current
        val calendar = remember { Calendar.getInstance() }
        val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
        var selectedDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->

                val pickedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                selectedDate = dateFormat.format(pickedDate.time)

                viewModel.loadCurrenciesForDate(dateFormat.format(pickedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Ustawienie maksymalnej daty do dzisiejszej
            datePicker.maxDate = System.currentTimeMillis()
            // Ustawienie pierwszego dnia tygodnia na poniedziałek
            datePicker.setFirstDayOfWeek(Calendar.MONDAY)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Data kursu: $selectedDate")
            Button(onClick = { datePickerDialog.show() }) {
                Text("Wybierz datę")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(savedCurrencies) { currency ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("history/${currency.code}") },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${getFlagForCurrency(currency.code)} ${currency.code}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            val rateInfo = exchangeRates.find { it.code == currency.code }

                            if (rateInfo != null && rateInfo.rate > 0.0) {
                                val dateText = if (rateInfo.actualDate != rateInfo.requestedDate)
                                    "Kurs z ${rateInfo.actualDate} (dla ${rateInfo.requestedDate})"
                                else
                                    "Kurs z ${rateInfo.actualDate}"

                                Text(
                                    text = "1 ${rateInfo.code} = ${"%.2f".format(rateInfo.rate)} PLN\n$dateText",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.removeCurrency(currency) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń"
                            )
                        }
                    }
                }
            }
        }
    }
}

