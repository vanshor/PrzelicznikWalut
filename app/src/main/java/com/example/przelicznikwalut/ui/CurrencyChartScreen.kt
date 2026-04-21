package com.example.przelicznikwalut.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.przelicznikwalut.R
import com.example.przelicznikwalut.ui.components.LineChart
import com.example.przelicznikwalut.viewmodel.HistoricalRatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyChartScreen(
    code: String,
    onBack: () -> Unit,
    vm: HistoricalRatesViewModel = viewModel()
) {
    val options = mapOf(
        stringResource(R.string.history_7_days) to 7,
        stringResource(R.string.history_1_month) to 31,
        stringResource(R.string.history_3_months) to 93,
        stringResource(R.string.history_1_year) to 365,
        stringResource(R.string.history_5_years) to 1825
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf(options.keys.first()) }

    LaunchedEffect(selectedLabel) {
        vm.loadHistory(code, options.getValue(selectedLabel))
    }

    val history by vm.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chart_title, code)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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

            Spacer(Modifier.height(32.dp))

            if (history.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val values = history.map { it.second }
                Text(
                    text = stringResource(R.string.chart_min_max, values.minOrNull() ?: 0.0, values.maxOrNull() ?: 0.0),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LineChart(
                    data = history,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
    }
}
