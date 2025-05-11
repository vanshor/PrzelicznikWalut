package com.example.przelicznikwalut.ui

// Ekran główny aplikacji. Umożliwia użytkownikowi wpisywanie waluty źródłowej i
// docelowej oraz kwoty do przeliczenia.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.przelicznikwalut.viewmodel.CurrencyConverterViewModel
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType


// Pobiera flagę dla danej waluty
fun getFlagForCurrency(currency: String): String {
    return when (currency) {
        "THB" -> "🇹🇭"
        "USD" -> "🇺🇸"
        "AUD" -> "🇦🇺"
        "HKD" -> "🇭🇰"
        "CAD" -> "🇨🇦"
        "NZD" -> "🇳🇿"
        "SGD" -> "🇸🇬"
        "EUR" -> "🇪🇺"
        "HUF" -> "🇭🇺"
        "CHF" -> "🇨🇭"
        "GBP" -> "🇬🇧"
        "UAH" -> "🇺🇦"
        "JPY" -> "🇯🇵"
        "CZK" -> "🇨🇿"
        "DKK" -> "🇩🇰"
        "ISK" -> "🇮🇸"
        "NOK" -> "🇳🇴"
        "SEK" -> "🇸🇪"
        "RON" -> "🇷🇴"
        "BGN" -> "🇧🇬"
        "TRY" -> "🇹🇷"
        "ILS" -> "🇮🇱"
        "CLP" -> "🇨🇱"
        "PHP" -> "🇵🇭"
        "MXN" -> "🇲🇽"
        "ZAR" -> "🇿🇦"
        "BRL" -> "🇧🇷"
        "MYR" -> "🇲🇾"
        "IDR" -> "🇮🇩"
        "INR" -> "🇮🇳"
        "KRW" -> "🇰🇷"
        "CNY" -> "🇨🇳"
        "XDR" -> "🌐"
        "PLN" -> "🇵🇱"
        else -> "🏳️"
    }
}


@Composable
fun CurrencyConverterScreen(viewModel: CurrencyConverterViewModel = viewModel()) {
    val amount by viewModel.amount.collectAsState()
    val sourceCurrency by viewModel.sourceCurrency.collectAsState()
    val targetCurrency by viewModel.targetCurrency.collectAsState()
    val result by viewModel.result.collectAsState()

    val currencies = listOf(
        "THB", "USD", "AUD", "HKD", "CAD", "NZD", "SGD", "EUR", "HUF", "CHF", "GBP", "UAH",
        "JPY", "CZK", "DKK", "ISK", "NOK", "SEK", "RON", "BGN", "TRY", "ILS", "CLP", "PHP",
        "MXN", "ZAR", "BRL", "MYR", "IDR", "INR", "KRW", "CNY", "XDR", "PLN"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Przelicznik walut",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kwota
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        // Pozwalaj tylko na cyfry, przecinek i kropkę
                        val filtered = it.filter { char -> char.isDigit() || char == ',' || char == '.' }
                        viewModel.onAmountChanged(filtered) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text("Kwota") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Kwota"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Z waluty
                DropdownSelector(
                    label = "Z waluty",
                    icon = Icons.Default.Translate,
                    options = currencies,
                    selected = sourceCurrency,
                    onOptionSelected = { viewModel.onSourceCurrencyChanged(it) }
                )

                IconButton(
                    onClick = { viewModel.swapCurrencies() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Zamień waluty"
                    )
                }

                // Na walutę
                DropdownSelector(
                    label = "Na walutę",
                    icon = Icons.Default.SwapHoriz,
                    options = currencies,
                    selected = targetCurrency,
                    onOptionSelected = { viewModel.onTargetCurrencyChanged(it) }
                )

                // Przycisk
                Button(
                    onClick = { viewModel.convert() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Przelicz")
                }
            }
        }

        if (result.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "💱 $result",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    icon: ImageVector,
    options: List<String>,
    selected: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge)

        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${getFlagForCurrency(selected)} $selected")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                options.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${getFlagForCurrency(currency)} $currency") },
                        onClick = {
                            onOptionSelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

