package com.example.catchmind.model



data class GameModel(
    val gameState: GameState, // 게임 상태 (그리기 또는 추측)
    val answerWord: String // 단어
)

enum class GameState {
    DRAWING, // 그리기 모드
    GUESSING // 추측 모드
}