package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendakanjirapido.model.KanjiItem

@Composable
fun MnemonicTransition(onStartMnemonic: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preparação",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nesta etapa você prepara o terreno para facilitar a memorização. Crie associações visuais e frases utilizando as componentes dos kanjis para não enxergar apenas rabiscos em uma tela. Isso acelerará o processo de aprendizagem.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartMnemonic,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Iniciar Revisão", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MnemonicScreen(
    index: Int,
    total: Int,
    kanjiItem: KanjiItem,
    onNext: (String) -> Unit,
    onBack: (String) -> Unit,
    onExit: (() -> Unit)? = null
) {
    var text by remember(kanjiItem) { mutableStateOf(kanjiItem.mnemonic) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (onExit != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onExit) {
                        Text(text = "✕ Sair", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            LinearProgressIndicator(
                progress = { (index + 1).toFloat() / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            Text(
                text = "Criação de Mnemônica (${index + 1}/$total)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = kanjiItem.kanji, fontSize = 72.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Significado: ${kanjiItem.significado}", style = MaterialTheme.typography.titleLarge)
                    if (kanjiItem.componentes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Componentes: ${kanjiItem.componentes}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Escreva sua mnemônica (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (index > 0) {
                OutlinedButton(
                    onClick = { onBack(text) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = "Voltar", fontSize = 16.sp)
                }
            }
            Button(
                onClick = { onNext(text) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(text = if (index + 1 == total) "Concluir" else "Avançar", fontSize = 16.sp)
            }
        }
    }
}
