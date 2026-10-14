package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendakanjirapido.model.KanjiItem
import com.example.aprendakanjirapido.ui.ChallengeViewModel


@Composable
fun ChallengeTransitionScreen(viewModel: ChallengeViewModel, onStartChallenge: () -> Unit) {
    val recallDuration = viewModel.recallDurationSeconds
    val gradeDuration = viewModel.gradeDurationSeconds
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Desafio",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "A revisão foi concluída. Agora começará o teste de conhecimento para verificar se você consegue se lembrar dos kanjis.\nVocê terá apenas $recallDuration segundos para tentar se lembrar do que viu, e apenas $gradeDuration segundos para confirmar se lembrou-se ou não. Então esteja atento e seja rápido.\n Você terá duas rodadas para tentar se lembrar de todos, caso não consiga, irá recomeçar.\n\nToque no botão abaixo para começar.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartChallenge,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Iniciar Desafio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecallScreen(viewModel: ChallengeViewModel, round: Int, kanjiItem: KanjiItem, timeLeft: Int, onFlip: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (viewModel.isGlobalChallengeMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.exitGlobalChallenge() }) {
                        Text(text = "✕ Sair", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            LinearProgressIndicator(
                progress = { viewModel.challengeProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Progresso: ${viewModel.challengeCurrentIndex} / ${viewModel.challengeProgress.let { 10 }}", // or dynamic
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Rodada $round - Recordação (3s)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tempo restante: ${timeLeft}s", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = kanjiItem.kanji, fontSize = 80.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Tente lembrar o significado!", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Button(
            onClick = onFlip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Virar Carta", fontSize = 16.sp)
        }
    }
}

@Composable
fun GradeScreen(viewModel: ChallengeViewModel, round: Int, kanjiItem: KanjiItem, timeLeft: Int, onGrade: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (viewModel.isGlobalChallengeMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.exitGlobalChallenge() }) {
                        Text(text = "✕ Sair", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            LinearProgressIndicator(
                progress = { viewModel.challengeProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Progresso: ${viewModel.challengeCurrentIndex}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Rodada $round - Avaliação (${timeLeft}s)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
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
                    Text(text = kanjiItem.kanji, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Significado: ${kanjiItem.significado}", style = MaterialTheme.typography.titleLarge)
                    if (kanjiItem.componentes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Componentes: ${kanjiItem.componentes}", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (kanjiItem.mnemonic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Mnemônica: ${kanjiItem.mnemonic}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Você conseguiu se lembrar?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onGrade(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(text = "Não lembrei", fontSize = 16.sp)
            }
            Button(
                onClick = { onGrade(true) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(text = "Lembrei!", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun RoundSummaryScreen(onStartRoundTwo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Fim da 1ª Rodada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Você teve dificuldades em alguns kanjis. Eles serão repetidos agora na 2ª Rodada.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartRoundTwo,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Iniciar 2ª Rodada", fontSize = 18.sp)
        }
    }
}
