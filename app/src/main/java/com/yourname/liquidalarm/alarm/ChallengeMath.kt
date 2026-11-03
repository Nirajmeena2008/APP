package com.yourname.liquidalarm.alarm

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChallengeMath(private val context: Context) {
    private var currentA = 0
    private var currentB = 0
    private var currentOp = 0
    private var currentAnswer = 0
    private var callback: ((Boolean) -> Unit)? = null
    private var attemptCount = 0
    private val maxAttempts = 3

    fun startChallenge(
        difficulty: Int = 1,
        onResult: (Boolean) -> Unit
    ) {
        callback = onResult
        attemptCount = 0
        generateQuestion(difficulty)
    }

    private fun generateQuestion(difficulty: Int) {
        val max = when (difficulty) {
            1 -> 10
            2 -> 20
            3 -> 50
            else -> 100
        }
        currentA = (1..max).random()
        currentB = (1..max).random()
        currentOp = (0..2).random()

        currentAnswer = when (currentOp) {
            0 -> currentA + currentB
            1 -> currentA - currentB
            else -> currentA * currentB
        }
    }

    fun getQuestion(): String {
        return when (currentOp) {
            0 -> "$currentA + $currentB = ?"
            1 -> "$currentA - $currentB = ?"
            else -> "$currentA × $currentB = ?"
        }
    }

    fun submitAnswer(input: String): Boolean {
        attemptCount++
        val success = try {
            input.trim().toInt() == currentAnswer
        } catch (e: Exception) {
            false
        }

        if (success || attemptCount >= maxAttempts) {
            callback?.invoke(success)
            callback = null
            return true
        }
        return false
    }

    fun getAttemptsRemaining(): Int = maxAttempts - attemptCount
}
