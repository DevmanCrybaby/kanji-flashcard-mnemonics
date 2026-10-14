package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendakanjirapido.model.KanjiItem

@Composable
fun GlobalReviewScreen(
    currentIndex: Int,
    totalCount: Int,
    kanjiItem: KanjiItem?,
    canGoBack: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
    onEditMnemonic: (KanjiItem) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Revisão Geral ($currentIndex de $totalCount)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = onExit) {
                Text(text = "✕ Sair", color = MaterialTheme.colorScheme.error)
            }
        }

        if (kanjiItem != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = kanjiItem.kanji, fontSize = 72.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Significado: ${kanjiItem.significado}", style = MaterialTheme.typography.titleLarge)
                        if (kanjiItem.componentes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Componentes: ${kanjiItem.componentes}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mnemonic & Edit button section clearly separated
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (kanjiItem.mnemonic.isNotBlank()) "Mnemônica: ${kanjiItem.mnemonic}" else "Sem mnemônica definida",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = { onEditMnemonic(kanjiItem) }) {
                                Text(text = "✏️ Editar Mnemônica")
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Buttons (Back & Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (canGoBack) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = "◀ Voltar", fontSize = 16.sp)
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(text = "Próximo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
