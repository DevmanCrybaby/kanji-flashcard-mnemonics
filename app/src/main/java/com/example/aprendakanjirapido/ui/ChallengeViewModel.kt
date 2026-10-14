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
    VICTORY,
    TUTORIAL_COMPLETED,
    MAIN_HUB,
    GLOBAL_REVIEW,
    DISCOVER_MNEMONIC,
    DISCOVER_REVIEW,
    DISCOVER_CHALLENGE
}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kanji_prefs", Context.MODE_PRIVATE)

    val reviewDurationSeconds = 10
    val recallDurationSeconds = 3
    val gradeDurationSeconds = 2

    var isGlobalChallengeMode = false
        private set

    private val _screenState = MutableStateFlow(ScreenState.WELCOME)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _kanjis = mutableListOf<KanjiItem>()
    val kanjis: List<KanjiItem> get() = _kanjis

    private val fullKanjiList = mutableListOf<KanjiItem>()

    private val _mnemonicIndex = MutableStateFlow(0)
    val mnemonicIndex: StateFlow<Int> = _mnemonicIndex.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val reviewQueue = mutableListOf<KanjiItem>()
    private val challengeQueue = mutableListOf<KanjiItem>()
    private val failedKanjis = mutableListOf<KanjiItem>()

    // Global Review state with index support
    private val globalReviewList = mutableListOf<KanjiItem>()
    private var globalReviewCurrentIndex = 0

    val globalReviewIndex: Int get() = globalReviewCurrentIndex + 1
    val globalReviewTotal: Int get() = globalReviewList.size.coerceAtLeast(1)
    val canGoBackGlobalReview: Boolean get() = globalReviewCurrentIndex > 0

    private val _currentKanji = MutableStateFlow<KanjiItem?>(null)
    val currentKanji: StateFlow<KanjiItem?> = _currentKanji.asStateFlow()

    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _totalFailedTotalCount = MutableStateFlow(0)
    val totalFailedTotalCount: StateFlow<Int> = _totalFailedTotalCount.asStateFlow()

    private val _seenKanjisCount = MutableStateFlow(10)
    val seenKanjisCount: StateFlow<Int> = _seenKanjisCount.asStateFlow()

    val totalKanjisCount: Int get() = fullKanjiList.size.coerceAtLeast(10)

    private val totalReviewSize = 10
    private var initialChallengeQueueSize = 10

    val reviewProgress: Float
        get() {
            val remaining = reviewQueue.size
            val reviewed = totalReviewSize - remaining
            return (reviewed.toFloat() / totalReviewSize.toFloat()).coerceIn(0f, 1f)
        }

    val reviewCurrentIndex: Int
        get() {
            val remaining = reviewQueue.size
            return (totalReviewSize - remaining).coerceIn(1, totalReviewSize)
        }

    val challengeProgress: Float
        get() {
            val remaining = challengeQueue.size
            val processed = initialChallengeQueueSize - remaining
            return (processed.toFloat() / initialChallengeQueueSize.toFloat()).coerceIn(0f, 1f)
        }

    val challengeCurrentIndex: Int
        get() {
            val remaining = challengeQueue.size
            return (initialChallengeQueueSize - remaining).coerceIn(1, initialChallengeQueueSize)
        }

    private var timerJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val full = KanjiRepository.loadKanjis(getApplication(), "lista_kanji_damage_componentes.csv")
        fullKanjiList.clear()
        fullKanjiList.addAll(full)

        val tutorial = KanjiRepository.loadKanjis(getApplication(), "tutorial_kanji_damage_componentes.csv")
        for (item in tutorial) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
        }
        _kanjis.clear()
        _kanjis.addAll(tutorial)

        val isTutorialDone = prefs.getBoolean("tutorial_completed", false)
        val seenCount = prefs.getInt("seen_count", 10)
        _seenKanjisCount.value = seenCount

        if (isTutorialDone) {
            _screenState.value = ScreenState.MAIN_HUB
        } else {
            _screenState.value = ScreenState.WELCOME
        }
    }

    fun startChallengeSetup() {
        _screenState.value = ScreenState.MNEMONIC_TRANSITION
    }

    fun proceedToMnemonicCreation() {
        isGlobalChallengeMode = false
        val tutorial = KanjiRepository.loadKanjis(getApplication(), "tutorial_kanji_damage_componentes.csv")
        for (item in tutorial) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
        }
        _kanjis.clear()
        _kanjis.addAll(tutorial)
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
            if (_screenState.value == ScreenState.DISCOVER_MNEMONIC) {
                val newSeen = (_seenKanjisCount.value + _kanjis.size).coerceAtMost(fullKanjiList.size)
                _seenKanjisCount.value = newSeen
                prefs.edit().putInt("seen_count", newSeen).apply()
                goToMainHub()
            } else {
                _screenState.value = ScreenState.REVIEW_TRANSITION
            }
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
        initialChallengeQueueSize = challengeQueue.size.coerceAtLeast(1)
        nextChallengeStep()
    }

    private fun nextChallengeStep() {
        if (challengeQueue.isEmpty()) {
            if (_currentRound.value == 1) {
                if (failedKanjis.isEmpty()) {
                    if (!isGlobalChallengeMode) {
                        prefs.edit().putBoolean("tutorial_completed", true).apply()
                        _screenState.value = ScreenState.TUTORIAL_COMPLETED
                    } else {
                        _screenState.value = ScreenState.VICTORY
                    }
                } else {
                    _screenState.value = ScreenState.ROUND_SUMMARY
                }
            } else {
                if (failedKanjis.isEmpty()) {
                    if (!isGlobalChallengeMode) {
                        prefs.edit().putBoolean("tutorial_completed", true).apply()
                        _screenState.value = ScreenState.TUTORIAL_COMPLETED
                    } else {
                        _screenState.value = ScreenState.VICTORY
                    }
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
        startTimer(2) {
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

    fun goToMainHub() {
        timerJob?.cancel()
        isGlobalChallengeMode = false
        _screenState.value = ScreenState.MAIN_HUB
    }

    // --- Global Review ("Revisar todos" - closable, with back & next) ---
    fun startGlobalReview() {
        timerJob?.cancel()
        globalReviewList.clear()
        val seenCount = _seenKanjisCount.value.coerceAtMost(fullKanjiList.size)
        val seenList = fullKanjiList.take(seenCount)
        for (item in seenList) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
        }
        globalReviewList.addAll(seenList)
        globalReviewCurrentIndex = 0
        _screenState.value = ScreenState.GLOBAL_REVIEW
        showGlobalReviewCard()
    }

    private fun showGlobalReviewCard() {
        if (globalReviewList.isEmpty()) {
            goToMainHub()
            return
        }
        _currentKanji.value = globalReviewList[globalReviewCurrentIndex]
    }

    fun nextGlobalReviewCard() {
        if (globalReviewCurrentIndex + 1 < globalReviewList.size) {
            globalReviewCurrentIndex++
            showGlobalReviewCard()
        } else {
            goToMainHub()
        }
    }

    fun previousGlobalReviewCard() {
        if (globalReviewCurrentIndex > 0) {
            globalReviewCurrentIndex--
            showGlobalReviewCard()
        }
    }

    fun exitGlobalReview() {
        goToMainHub()
    }

    // --- Desafiar-se (Global Challenge reusing Tutorial Challenge engine) ---
    fun startGlobalChallenge() {
        timerJob?.cancel()
        isGlobalChallengeMode = true
        val seenCount = _seenKanjisCount.value.coerceAtMost(fullKanjiList.size)
        val seenList = fullKanjiList.take(seenCount)
        if (seenList.isEmpty()) {
            goToMainHub()
            return
        }
        for (item in seenList) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
            item.failCount = 0
        }
        _kanjis.clear()
        _kanjis.addAll(seenList)
        startRound(1)
    }

    fun exitGlobalChallenge() {
        timerJob?.cancel()
        isGlobalChallengeMode = false
        goToMainHub()
    }

    // --- Discover New Kanjis ("Continuar descobrindo") ---
    fun startDiscovering() {
        timerJob?.cancel()
        isGlobalChallengeMode = false
        val currentSeen = _seenKanjisCount.value
        val nextBatch = fullKanjiList.drop(currentSeen).take(10)
        if (nextBatch.isEmpty()) {
            goToMainHub()
            return
        }
        for (item in nextBatch) {
            item.mnemonic = prefs.getString("mnemonic_${item.kanji}", "") ?: ""
        }
        _kanjis.clear()
        _kanjis.addAll(nextBatch)
        _mnemonicIndex.value = 0
        _screenState.value = ScreenState.DISCOVER_MNEMONIC
    }

    fun exitDiscovery() {
        timerJob?.cancel()
        goToMainHub()
    }

    fun updateMnemonic(kanjiChar: String, newMnemonic: String) {
        prefs.edit().putString("mnemonic_$kanjiChar", newMnemonic).apply()
        fullKanjiList.find { it.kanji == kanjiChar }?.mnemonic = newMnemonic
        _kanjis.find { it.kanji == kanjiChar }?.mnemonic = newMnemonic
        _currentKanji.value?.let { if (it.kanji == kanjiChar) it.mnemonic = newMnemonic }
    }

    fun resetApp() {
        timerJob?.cancel()
        prefs.edit().clear().apply()
        loadInitialData()
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
