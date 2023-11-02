package com.cs211d.fallingstars

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.random.Random
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {
    private lateinit var gameSpace: FrameLayout
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var scoreTextView: TextView

    private var score = 0
    private var total = 0
    private var isGameRunning = false
    private var spawnIntervalLow = 500L
    private var spawnIntervalHigh = 3000L


    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSpace = findViewById(R.id.game_space)
        startButton = findViewById(R.id.start_button)
        resetButton = findViewById(R.id.reset_button)
        scoreTextView = findViewById(R.id.score_textview)
        scoreTextView.text = getString(R.string.score_label, score, total)

        startButton.setOnClickListener { startGame() }
        resetButton.setOnClickListener { resetGame() }
    }

    private fun startGame() {
        if (!isGameRunning) {
            isGameRunning = true
            score = 0
            total = 0
            scoreTextView.text = getString(R.string.score_label, score, total)
            startButton.isEnabled = false

            /* spawn falling shapes using a timer */
            // interval specifies how frequently a shape will be spawned (the spawn rate)
            val interval = Random.nextLong(spawnIntervalLow, spawnIntervalHigh)
            timer = Timer()
            timer?.scheduleAtFixedRate(timerTask {
                runOnUiThread {
                    if (isGameRunning) {
                        spawnFallingShape()
                    } else {
                        timer?.cancel()
                    }
                }
            }, 0, interval)
        }
    }

    private fun spawnFallingShape() {
        val fallingShape = ImageView(this)
        fallingShape.setImageResource(R.drawable.circle)
        fallingShape.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        /* YOUR STEP 2 CODE HERE */

        gameSpace.addView(fallingShape)

        /* YOUR STEP 3 CODE HERE */



    }

    private fun resetGame() {
        isGameRunning = false
        startButton.isEnabled = true
        score = 0
        total = 0
        scoreTextView.text = getString(R.string.score_label, score, total)
        gameSpace.removeAllViews()
    }
}
