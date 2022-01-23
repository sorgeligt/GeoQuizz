package com.sorgeligt.geoquizz

import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    var answeredQuestions = 0
    var rightAnsweredQuestions = 0

    class QuestionWithState(
        var question: Question,
        var isAnsweredFlag: Boolean = false,
        var isCheatingFlag: Boolean = false
    )

    var questionBank = listOf(
        QuestionWithState(Question(R.string.question_australia, true)),
        QuestionWithState(Question(R.string.question_oceans, true)),
        QuestionWithState(Question(R.string.question_mideast, false)),
        QuestionWithState(Question(R.string.question_africa, false)),
        QuestionWithState(Question(R.string.question_americas, true)),
        QuestionWithState(Question(R.string.question_asia, true))
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].question.answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].question.textResId
    var currentQuestionAnsweredFlag: Boolean
        get() = questionBank[currentIndex].isAnsweredFlag
        set(value) {
            questionBank[currentIndex].isAnsweredFlag = value
        }
    var currentQuestionCheatingFlag: Boolean
        get() = questionBank[currentIndex].isCheatingFlag
        set(value) {
            questionBank[currentIndex].isCheatingFlag = value
        }


    fun incAnsweredQuestionCounter() = answeredQuestions++
    fun incRightAnsweredQuestionCounter() = rightAnsweredQuestions++

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1 + questionBank.size) % questionBank.size
    }

    fun questionBankSize() = questionBank.size

    fun clearModel() {
        questionBank = listOf(
            QuestionWithState(Question(R.string.question_australia, true)),
            QuestionWithState(Question(R.string.question_oceans, true)),
            QuestionWithState(Question(R.string.question_mideast, false)),
            QuestionWithState(Question(R.string.question_africa, false)),
            QuestionWithState(Question(R.string.question_americas, true)),
            QuestionWithState(Question(R.string.question_asia, true))
        )
        answeredQuestions = 0
        rightAnsweredQuestions = 0
        currentIndex = 0
    }
}