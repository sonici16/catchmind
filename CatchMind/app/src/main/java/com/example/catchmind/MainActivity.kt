package com.example.catchmind

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.catchmind.databinding.ActivityMainBinding
import com.example.catchmind.model.GameModel
import com.example.catchmind.model.GameState
import com.example.catchmind.viewmodel.MainViewModel
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // ViewModel의 LiveData를 관찰하여 UI 업데이트
        viewModel.gameModelLiveData.observe(this, Observer { mainModel ->
            binding.wordTextView.text = mainModel.answerWord
            binding.drawingView.setDrawing()
        })

        // 초기화 버튼 클릭 시 ViewModel 업데이트
        binding.drawButton.setOnClickListener {
           binding.drawingView.setDrawing()
        }


        // GuessingViewModel의 gameModelLiveData를 관찰하여 UI 업데이트
        viewModel.gameModelLiveData.observe(this, Observer { gameModel ->
            // 게임 추측 모드에 따른 UI 업데이트
            updateUI(gameModel)
        })

        viewModel.gameStateLiveData.observe(this, Observer { gameState ->
            when (gameState) {
                GameState.GUESSING -> {
                    // 추측 모드일 때 그림 그리기를 비활성화
                    disableDrawing()
                }
                GameState.DRAWING -> {
                    // 그리기 모드일 때 그림 그리기를 활성화
                    enableDrawing()
                }
            }
        })

        // 정답을 맞췄을 때 출제자를 맞추는 사람으로 변경
        binding.sendButton.setOnClickListener {

            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val userAnswer = binding.drawingEditText.text.toString()
            val correctAnswer = viewModel.getCorrectAnswer() // ViewModel에서 정답을 가져옴

            // 정답을 비교하고 정답 여부에 따른 동작 수행
            if (isCorrectAnswer(userAnswer, correctAnswer)) {
                // 정답인 경우, 여기에서  정답자를 출제자로 변경할 수 있음
                viewModel.changeToDrawer()
            } else {
                // 정답이 아닌 경우, 다른 동작을 수행할 수 있음
                // 예: 오답 메시지 표시 등
            }
        }

        binding.colorPickerButton.setOnClickListener {
            //colorpickerDialog

            ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(R.string.ok,
                    ColorEnvelopeListener { envelope, fromUser -> binding.drawingView.setColor(envelope.color) })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(false) // the default value is true.
                .attachBrightnessSlideBar(false)  // the default value is true.
                .show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateUI(gameModel: GameModel) {
        // gameModel에 따른 UI 업데이트
        var gameState = gameModel.gameState
                when (gameState) {
                    GameState.GUESSING -> {
                        // 추측 모드에서 정답 버튼 활성화, 그리기 기능을 비활성
                        binding.sendButton.isEnabled = true
                        binding.drawButton.isEnabled = false
                        binding.wordTextView.text = "그림을 맞춰주세요"
                        enableDrawing()
                    }
                    GameState.DRAWING -> {
                        // 그리기 모드에서는 정답 버튼 비활성 그리기 기능을 활성화
                        binding.sendButton.isEnabled = false
                        binding.drawButton.isEnabled = true
                        binding.wordTextView.visibility = View.VISIBLE
                        disableDrawing()
                    }
                }
    }

    private fun enableDrawing() {
        // 그림 그리기 활성화 관련 로직을 구현
        binding.drawingView.setDrawingEnabled(true)
    }

    private fun disableDrawing() {
        // 그림 그리기 비활성화 관련 로직을 구현
        binding.drawingView.setDrawingEnabled(false)
    }

    private fun isCorrectAnswer(userAnswer: String, correctAnswer: String): Boolean {
        // 정답을 비교하는 로직을 구현
        // 여기서는 대소문자를 무시하고 비교합니다.
        return userAnswer.equals(correctAnswer, ignoreCase = true)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }

}