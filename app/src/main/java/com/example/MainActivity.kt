package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.MediFindApp
import com.example.ui.theme.MediFindTheme
import com.example.viewmodel.MediFindViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MediFindViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediFindTheme {
                MediFindApp(viewModel = viewModel)
            }
        }
    }
}
