package com.example.przelicznikwalut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.przelicznikwalut.ui.CurrencyConverterScreen
import com.example.przelicznikwalut.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                CurrencyConverterScreen()
            }
        }
    }
}
