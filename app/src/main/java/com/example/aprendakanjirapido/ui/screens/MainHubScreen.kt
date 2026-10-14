package com.example.aprendakanjirapido.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendakanjirapido.ui.ChallengeViewModel

@Composable
fun MainHubScreen(viewModel: ChallengeViewModel) {
    val context = LocalContext.current
    val seenCount by viewModel.seenKanjisCount.collectAsState()
    val totalCount = viewModel.totalKanjisCount
    val progress = (seenCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    // Load Sakura Bitmaps from assets
    val dryTreeBitmap = remember {
        try {
            context.assets.open("arvore sakura seca.png").use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
    }
    val bloomingTreeBitmap = remember {
        try {
            context.assets.open("arvore sakura com flores.png").use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Seu Bosque de Kanjis 🌸",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sakura Tree Blooming Visual with overlay mask
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Base layer: Dry tree
                dryTreeBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Árvore Seca",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Blooming layer overlay with alpha corresponding to progress
                bloomingTreeBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Árvore Florindo",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(progress)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Indicator
            Text(
                text = "$seenCount / $totalCount Kanjis vistos (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.startDiscovering() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Continuar Descobrindo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.startGlobalReview() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Revisar Todos", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.startGlobalChallenge() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Desafiar-se", fontSize = 16.sp)
                }

                TextButton(
                    onClick = {
                        Toast.makeText(context, "Metodologia: Aprendizado natural por decomposição e mnemônicas.", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sobre os Kanjis e Metodologia")
                }
            }
        }
    }
}
