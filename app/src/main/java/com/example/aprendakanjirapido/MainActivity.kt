package com.example.aprendakanjirapido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.aprendakanjirapido.ui.ChallengeViewModel
import com.example.aprendakanjirapido.ui.screens.ChallengeAppRoot
import com.example.aprendakanjirapido.ui.theme.AprendaKanjiRapidoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AprendaKanjiRapidoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ChallengeAppRoot(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
