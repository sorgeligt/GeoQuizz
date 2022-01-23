package com.sorgeligt.geoquizz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView

private const val EXTRA_ANSWER_IS_TRUE =
    "com.sorgeligt.geoquizz.answer_is_true"
private const val EXTRA_ANSWER_SHOWN =
    "com.sorgeligt.geoquizz.answer_shown"
private const val IS_CHEATER = "is_cheater"

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private lateinit var answerTextView: TextView
    private lateinit var getAnswerButton: Button
    private var isUserGetAnswer:Boolean = false
    private var isCheater: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        getAnswerButton = findViewById(R.id.show_answer_button)
        getAnswerButton.setOnClickListener {
            showAnswerForUser()
        }
        isCheater = savedInstanceState?.getBoolean(IS_CHEATER) ?: false
        if (isCheater) {
            showAnswerForUser()
        }
    }

    private fun showAnswerForUser() {
        isUserGetAnswer = true
        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }
        answerTextView.setText(answerText)
        setAnswerShownResult(true)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_CHEATER, isUserGetAnswer)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}