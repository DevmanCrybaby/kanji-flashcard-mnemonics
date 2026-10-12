package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TutorialCompletedScreen(
    onOpenMethodology: () -> Unit,
    onContinueChallenge: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState), // Permite rolar se o texto for grande
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tutorial Concluído! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card ou Container para o texto explicativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Este foi apenas o tutorial, agora começa o desafio.\n\n" +
                                "Pelos próximos dias, você será atropelado por novos kanjis, até que você tenha visto todos os kanjis de uso cotidiano, e poderá revisar suas mnemônicas durante as etapas de preparação.\n\n" +
                                "Serão muitos kanjis tentando se acomodar na sua mente, mas se você não demorar elaborando as mnemônicas, você é capaz de ver todos estes kanjis em apenas 6 dias com 2 horas por dia.\n\n" +
                                "Não se foque demais em pensar na mnemônica perfeita, ou até em manter a mesma, elas devem vir da maneira mais natural à sua mente.\n\n" +
                                "A visualização repetida do mesmo kanji como componente de outros vai te ajudar a memorizar grande parte deles naturalmente.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botões de Ação na parte inferior
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onOpenMethodology,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Sobre os Kanjis e Metodologia", fontSize = 16.sp)
            }

            Button(
                onClick = onContinueChallenge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Continuar o Desafio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}