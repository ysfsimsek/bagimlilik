package com.opendrip.bagimlilik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.opendrip.bagimlilik.ui.screens.MainScreen
import com.opendrip.bagimlilik.ui.theme.BagimlilikTheme

// Not: Hilt kurulumu tamamlandığında @AndroidEntryPoint buraya eklenecektir.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BagimlilikTheme {
                MainScreen()
            }
        }
    }
}
