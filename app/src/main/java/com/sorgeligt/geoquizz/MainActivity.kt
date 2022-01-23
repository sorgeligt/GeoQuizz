package com.sorgeligt.geoquizz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CHEAT_HINTS_COUNTER = "hints_count"

private const val REQUEST_CODE_CHEAT = 0
private const val EXTRA_ANSWER_SHOWN = "com.sorgeligt.geoquizz.answer_shown"
private const val DEFAULT_HINTS_NUMBER = 3

class MainActivity : AppCompatActivity() {
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    var resultMenuFlag: Boolean = false
    var cheatCounter: Int = DEFAULT_HINTS_NUMBER

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var apiTextView: TextView
    private lateinit var cheatHintsTextView: TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        cheatCounter = savedInstanceState?.getInt(KEY_CHEAT_HINTS_COUNTER, DEFAULT_HINTS_NUMBER)
            ?: DEFAULT_HINTS_NUMBER

        setContentView(R.layout.activity_main)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        apiTextView = findViewById(R.id.api_text_view)
        cheatHintsTextView = findViewById(R.id.hint_cheating_remained)
        cheatHintsTextView.text = "$cheatCounter hints left"
        apiTextView.text = "API LEVEL: ${Build.VERSION.SDK_INT}"
        cheatButton.setOnClickListener { view ->
            val intent =
                CheatActivity.newIntent(this@MainActivity, quizViewModel.currentQuestionAnswer)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }
        trueButton.setOnClickListener {
            if (resultMenuFlag) {
                quizViewModel.clearModel()
                nextButton.visibility = View.VISIBLE
                prevButton.visibility = View.VISIBLE
                trueButton.text = getString(R.string.true_button)
                falseButton.visibility = View.VISIBLE
                cheatButton.visibility = View.VISIBLE
                cheatHintsTextView.visibility = View.VISIBLE
                cheatButton.isEnabled = true
                updateQuestion()
                resultMenuFlag = false
            } else {
                quizViewModel.incAnsweredQuestionCounter()
                quizViewModel.currentQuestionAnsweredFlag = true
                checkAnswer(true)
                blockAnswerButtons()
            }
        }
        falseButton.setOnClickListener {
            quizViewModel.incAnsweredQuestionCounter()
            checkAnswer(false)
            blockAnswerButtons()
            quizViewModel.currentQuestionAnsweredFlag = true
        }
        updateQuestion()
    }

    private fun blockAnswerButtons() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        trueButton.isEnabled = !quizViewModel.currentQuestionAnsweredFlag

        falseButton.isEnabled = !quizViewModel.currentQuestionAnsweredFlag
        if (quizViewModel.answeredQuestions == quizViewModel.questionBankSize()) {
            restartView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun restartView() {
        nextButton.visibility = View.GONE
        prevButton.visibility = View.GONE
        trueButton.text = "Restart"
        trueButton.isEnabled = true
        falseButton.visibility = View.GONE
        cheatButton.visibility = View.GONE
        cheatHintsTextView.visibility = View.GONE
        questionTextView.text =
            "You answered  ${quizViewModel.rightAnsweredQuestions * 100 / quizViewModel.answeredQuestions}% " +
                    "of the questions correctly"
        resultMenuFlag = true
    }


    private fun checkAnswer(userAns: Boolean) {
        val toast: Int
        if (quizViewModel.currentQuestionCheatingFlag) {
            toast = R.string.judgment_toast
            quizViewModel.incRightAnsweredQuestionCounter()
        } else if (quizViewModel.currentQuestionAnswer == userAns) {
            toast = R.string.correct_toast
            quizViewModel.incRightAnsweredQuestionCounter()
        } else {
            toast = R.string.incorrect_toast
        }
        Toast.makeText(
            this,
            toast,
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.currentQuestionCheatingFlag =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (quizViewModel.currentQuestionCheatingFlag) {
                cheatCounter--
                cheatHintsTextView.text = "$cheatCounter hints left"
                if (cheatCounter <= 0) {
                    cheatButton.isEnabled = false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CHEAT_HINTS_COUNTER, cheatCounter)
    }

}