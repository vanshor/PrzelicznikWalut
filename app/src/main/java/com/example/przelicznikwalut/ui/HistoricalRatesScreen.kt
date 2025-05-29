package com.example.przelicznikwalut.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.przelicznikwalut.viewmodel.HistoricalRatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalRatesScreen(
    code: String,
    onBack: () -> Unit,
    vm: HistoricalRatesViewModel = viewModel()
) {
    val options = mapOf(
        "7 dni" to 7,
        "1 miesiąc" to 31,
        "3 miesiące" to 93,
        "1 rok" to 365,
        "5 lat" to 1825
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf("7 dni") }

    LaunchedEffect(selectedLabel) {
        vm.loadHistory(code, options.getValue(selectedLabel))
    }

    val history by vm.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historia $code") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wstecz")
                    }
                }
            )
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(selectedLabel) }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.keys.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedLabel = label
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(history) { (date, value) ->
                        Card(
                            Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(date)
                                Text("1 $code = ${"%.4f".format(value)} PLN")
                            }
                        }
                    }
                }
            }
        }
    }
}
