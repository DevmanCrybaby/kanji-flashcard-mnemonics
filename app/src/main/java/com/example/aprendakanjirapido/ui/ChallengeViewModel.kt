package com.example.aprendakanjirapido.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprendakanjirapido.data.KanjiRepository
import com.example.aprendakanjirapido.model.KanjiItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenState {
    WELCOME,
    MNEMONIC_TRANSITION,
    MNEMONIC_CREATION,
    REVIEW_TRANSITION,
    CHALLENGE_REVIEW,
    CHALLENGE_TRANSITION,
    CHALLENGE_RECALL,
    CHALLENGE_GRADE,
    ROUND_SUMMARY,
    DEFEAT,
    VICTORY
}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kanji_prefs", Context.MODE_PRIVATE)

    private val _screenState = MutableStateFlow(ScreenState.WELCOME)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _kanjis = mutableListOf<KanjiItem>()
    val kanjis: List<KanjiItem> get() = _kanjis

    private val _mnemonicIndex = MutableStateFlow(0)
    val mnemonicIndex: StateFlow<Int> = _mnemonicIndex.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val reviewQueue = mutableListOf<KanjiItem>()
    private val challengeQueue = mutableListOf<KanjiItem>()
    private val failedKanjis = mutableListOf<KanjiItem>()

    private val _currentKanji = MutableStateFlow<KanjiItem?>(null)
    val currentKanji: StateFlow<KanjiItem?> = _currentKanji.asStateFlow()

    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _totalFailedTotalCount = MutableStateFlow(0)
    val totalFailedTotalCount: StateFlow<Int> = _totalFailedTotalCount.asStateFlow()

    val recallDurationSeconds = 3
    val reviewDurationSeconds = 10
    val gradeDurationSeconds = 2

    private var timerJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        val loaded = KanjiRepository.loadKanjis(getApplication())
        for (item in loaded) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
        }
        _kanjis.clear()
        _kanjis.addAll(loaded.take(10))
    }

    fun startChallengeSetup() {
        _screenState.value = ScreenState.MNEMONIC_TRANSITION
    }

    fun proceedToMnemonicCreation() {
        // Reload in case mnemonics were updated
        loadData()
        _mnemonicIndex.value = 0
        _screenState.value = ScreenState.MNEMONIC_CREATION
    }

    fun saveMnemonicAndNext(index: Int, mnemonicText: String) {
        if (index in _kanjis.indices) {
            val kanji = _kanjis[index]
            kanji.mnemonic = mnemonicText
            prefs.edit().putString("mnemonic_${kanji.kanji}", mnemonicText).apply()
        }
        if (index + 1 < _kanjis.size) {
            _mnemonicIndex.value = index + 1
        } else {
            _screenState.value = ScreenState.REVIEW_TRANSITION
        }
    }

    fun previousMnemonic(index: Int, currentText: String) {
        if (index in _kanjis.indices) {
            val kanji = _kanjis[index]
            kanji.mnemonic = currentText
            prefs.edit().putString("mnemonic_${kanji.kanji}", currentText).apply()
        }
        if (index > 0) {
            _mnemonicIndex.value = index - 1
        }
    }

    fun proceedToReview() {
        reviewQueue.clear()
        reviewQueue.addAll(_kanjis)
        nextReviewStep()
    }

    private fun nextReviewStep() {
        if (reviewQueue.isEmpty()) {
            _screenState.value = ScreenState.CHALLENGE_TRANSITION
            return
        }
        val kanji = reviewQueue.removeAt(0)
        _currentKanji.value = kanji
        _screenState.value = ScreenState.CHALLENGE_REVIEW
        startTimer(reviewDurationSeconds) {
            nextReviewStep()
        }
    }

    fun skipReviewCard() {
        timerJob?.cancel()
        nextReviewStep()
    }

    fun proceedToChallenge() {
        startRound(1)
    }

    private fun startRound(round: Int) {
        _currentRound.value = round
        challengeQueue.clear()
        if (round == 1) {
            failedKanjis.clear()
            challengeQueue.addAll(_kanjis)
        } else {
            challengeQueue.addAll(failedKanjis)
            failedKanjis.clear()
        }
        nextChallengeStep()
    }

    private fun nextChallengeStep() {
        if (challengeQueue.isEmpty()) {
            if (_currentRound.value == 1) {
                if (failedKanjis.isEmpty()) {
                    _screenState.value = ScreenState.VICTORY
                } else {
                    _screenState.value = ScreenState.ROUND_SUMMARY
                }
            } else {
                if (failedKanjis.isEmpty()) {
                    _screenState.value = ScreenState.VICTORY
                } else {
                    _totalFailedTotalCount.value = failedKanjis.sumOf { it.failCount }
                    _screenState.value = ScreenState.DEFEAT
                }
            }
            return
        }

        val kanji = challengeQueue.removeAt(0)
        _currentKanji.value = kanji
        startRecallPhase()
    }

    fun startRoundTwo() {
        startRound(2)
    }

    private fun startRecallPhase() {
        _screenState.value = ScreenState.CHALLENGE_RECALL
        startTimer(recallDurationSeconds) {
            startGradePhase()
        }
    }

    fun flipCardToGrade() {
        timerJob?.cancel()
        startGradePhase()
    }

    private fun startGradePhase() {
        _screenState.value = ScreenState.CHALLENGE_GRADE
        startTimer(gradeDurationSeconds) {
            gradeKanji(remembered = false)
        }
    }

    fun gradeKanji(remembered: Boolean) {
        timerJob?.cancel()
        val kanji = _currentKanji.value ?: return
        if (!remembered) {
            kanji.failCount++
            failedKanjis.add(kanji)
        }
        nextChallengeStep()
    }

    private fun startTimer(seconds: Int, onTimeout: () -> Unit) {
        timerJob?.cancel()
        _timeLeft.value = seconds
        timerJob = viewModelScope.launch {
            for (s in seconds downTo 1) {
                delay(1000L)
                _timeLeft.value = s - 1
            }
            onTimeout()
        }
    }

    fun resetApp() {
        timerJob?.cancel()
        _kanjis.forEach { it.failCount = 0 }
        failedKanjis.clear()
        reviewQueue.clear()
        challengeQueue.clear()
        _screenState.value = ScreenState.WELCOME
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
