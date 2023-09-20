package com.example.catchmind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.catchmind.model.GameModel
import com.example.catchmind.model.GameState

class MainViewModel : ViewModel() {
    val gameModelLiveData = MutableLiveData<GameModel>()

    // 게임 상태 LiveData 추가
    private val _gameStateLiveData = MutableLiveData<GameState>()

    val gameStateLiveData: LiveData<GameState>
        get() = _gameStateLiveData

    private var currentGameState: GameState = GameState.GUESSING

    init {
        // 초기 게임 데이터 설정
        gameModelLiveData.value = GameModel(currentGameState,"사과")
        _gameStateLiveData.value = currentGameState
    }

    fun updateDrawing() {
        val currentModel = gameModelLiveData.value
        gameModelLiveData.postValue(currentModel)
    }

    fun getCorrectAnswer(): String {
        // 게임 상태에 따라 다른 정답 반환
        return gameModelLiveData.value!!.answerWord
    }

    // 출제자를 맞추는 사람으로 변경하는 메서드
    fun changeToGuesser() {
        currentGameState = GameState.GUESSING
        _gameStateLiveData.value = currentGameState
        changeAnswer()
    }

    fun changeToDrawer() {
        currentGameState = GameState.DRAWING
        _gameStateLiveData.value = currentGameState
        changeAnswer()
    }

    private fun changeAnswer(){
        gameModelLiveData.value = GameModel(currentGameState,"바나나")
        _gameStateLiveData.value = currentGameState
    }
}