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

// Pobiera flagę dla danej waluty
fun getFlagForCurrency(currency: String): String {
    return when (currency) {
        "USD" -> "🇺🇸"
        "EUR" -> "🇪🇺"
        "PLN" -> "🇵🇱"
        "GBP" -> "🇬🇧"
        "CHF" -> "🇨🇭"
        "NOK" -> "🇳🇴"
        "SEK" -> "🇸🇪"
        "DKK" -> "🇩🇰"
        "JPY" -> "🇯🇵"
        "CZK" -> "🇨🇿"
        "AUD" -> "🇦🇺"
        "CAD" -> "🇨🇦"
        "HUF" -> "🇭🇺"
        "CNY" -> "🇨🇳"
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
        "PLN", "USD", "EUR", "GBP", "CHF", "JPY", "CZK", "NOK", "SEK",
        "DKK", "CAD", "AUD", "HUF", "CNY"
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
                    onValueChange = { viewModel.onAmountChanged(it) },
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
            Text(
                text = result,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1B5E20),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                modifier = Modifier.fillMaxWidth()
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

