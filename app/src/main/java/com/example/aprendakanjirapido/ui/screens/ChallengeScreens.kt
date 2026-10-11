package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendakanjirapido.model.KanjiItem
import com.example.aprendakanjirapido.ui.ChallengeViewModel
import com.example.aprendakanjirapido.ui.ScreenState

@Composable
fun ChallengeAppRoot(viewModel: ChallengeViewModel) {
    val screenState by viewModel.screenState.collectAsState()
    val kanjis = viewModel.kanjis
    val mnemonicIndex by viewModel.mnemonicIndex.collectAsState()
    val currentKanji by viewModel.currentKanji.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val totalFailedCount by viewModel.totalFailedTotalCount.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (screenState) {
            ScreenState.WELCOME -> {
                WelcomeScreen(onStart = { viewModel.startChallengeSetup() })
            }
            ScreenState.MNEMONIC_TRANSITION -> {
                MnemonicTransition(onStartMnemonic = { viewModel.proceedToMnemonicCreation() })
            }
            ScreenState.MNEMONIC_CREATION -> {
                if (kanjis.isNotEmpty() && mnemonicIndex in kanjis.indices) {
                    MnemonicScreen(
                        index = mnemonicIndex,
                        total = kanjis.size,
                        kanjiItem = kanjis[mnemonicIndex],
                        onNext = { mnemonic ->
                            viewModel.saveMnemonicAndNext(mnemonicIndex, mnemonic)
                        },
                        onBack = { currentText ->
                            viewModel.previousMnemonic(mnemonicIndex, currentText)
                        }
                    )
                }
            }
            ScreenState.REVIEW_TRANSITION -> {
                ReviewTransitionScreen(viewModel = viewModel, onStartReview = {viewModel.proceedToReview() })
            }
            ScreenState.CHALLENGE_REVIEW -> {
                if (currentKanji != null) {
                    ReviewScreen(
                        kanjiItem = currentKanji!!,
                        timeLeft = timeLeft,
                        onSkip = { viewModel.skipReviewCard() }
                    )
                }
            }
            ScreenState.CHALLENGE_TRANSITION -> {
                ChallengeTransitionScreen(viewModel = viewModel, onStartChallenge = {viewModel.proceedToChallenge() })
            }
            ScreenState.CHALLENGE_RECALL -> {
                if (currentKanji != null) {
                    RecallScreen(
                        round = currentRound,
                        kanjiItem = currentKanji!!,
                        timeLeft = timeLeft,
                        onFlip = { viewModel.flipCardToGrade() }
                    )
                }
            }
            ScreenState.CHALLENGE_GRADE -> {
                if (currentKanji != null) {
                    GradeScreen(
                        round = currentRound,
                        kanjiItem = currentKanji!!,
                        timeLeft = timeLeft,
                        onGrade = { remembered -> viewModel.gradeKanji(remembered) }
                    )
                }
            }
            ScreenState.ROUND_SUMMARY -> {
                RoundSummaryScreen(
                    onStartRoundTwo = { viewModel.startRoundTwo() }
                )
            }
            ScreenState.DEFEAT -> {
                DefeatScreen(
                    totalFailed = totalFailedCount,
                    onRestart = { viewModel.resetApp() }
                )
            }
            ScreenState.VICTORY -> {
                VictoryScreen(
                    onRestart = { viewModel.resetApp() }
                )
            }
        }
    }
}

@Composable
fun MnemonicTransition(onStartMnemonic: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aprenda Kanji Rápido",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Domine os 10 primeiros kanjis com mnemônicas e desafios espaçados.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Começar Desafio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MnemonicScreen(
    index: Int,
    total: Int,
    kanjiItem: KanjiItem,
    onNext: (String) -> Unit,
    onBack: (String) -> Unit
) {
    var text by remember(kanjiItem) { mutableStateOf(kanjiItem.mnemonic) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
fun ReviewTransitionScreen(viewModel: ChallengeViewModel, onStartReview: () -> Unit) {
    val reviewDuration = viewModel.reviewDurationSeconds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Revisão",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Todas as mnemônicas foram criadas. Agora você passará pela etapa de revisão de todos os cartões.\nVocê terá $reviewDuration segundos para revisar cada cartão. \n\nToque no botão abaixo para começar.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartReview,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Iniciar Revisão", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReviewScreen(kanjiItem: KanjiItem, timeLeft: Int, onSkip: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Etapa de Revisão (10s)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tempo restante: ${timeLeft}s", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
                    if (kanjiItem.mnemonic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Mnemônica: ${kanjiItem.mnemonic}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }

        Button(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Próximo / Pular", fontSize = 16.sp)
        }
    }
}

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
fun RecallScreen(round: Int, kanjiItem: KanjiItem, timeLeft: Int, onFlip: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
fun GradeScreen(round: Int, kanjiItem: KanjiItem, timeLeft: Int, onGrade: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

@Composable
fun DefeatScreen(totalFailed: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Derrota!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Você não conseguiu se lembrar dos kanjis na 2ª rodada.\nTotal de falhas acumuladas: $totalFailed",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Começar Novamente", fontSize = 18.sp)
        }
    }
}

@Composable
fun VictoryScreen(onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Vitória!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Parabéns! Você dominou todos os kanjis do desafio!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Começar Novamente", fontSize = 18.sp)
        }
    }
}
