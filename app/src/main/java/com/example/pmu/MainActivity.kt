package com.example.pmu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pmu.viewmodel.NewsController
import androidx.compose.material3.MaterialTheme
import com.example.pmu.jetpackcompose.JetPackComposeController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val model = NewsController()
                JetPackComposeController().NewsScreen(model)
            }
        }
    }
}
