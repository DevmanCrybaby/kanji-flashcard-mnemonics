package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

            ScreenState.TUTORIAL_COMPLETED -> {
                TutorialCompletedScreen(
                    onOpenMethodology = { /* Botão morto por enquanto */ },
                    onContinueChallenge = { /* Botão morto por enquanto */ }
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
