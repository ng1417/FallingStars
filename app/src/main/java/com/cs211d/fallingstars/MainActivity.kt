package com.cs211d.fallingstars

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.random.Random
import kotlin.concurrent.timerTask
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var gameSpace: FrameLayout
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var scoreTextView: TextView
    private var startTime: Long = 0
    private var stopTime: Long = 0

    private var score = 0
    private var total = 0
    private var isGameRunning = false
    private var spawnIntervalLow = 500L
    private var spawnIntervalHigh = 3000L
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState !=null) {
            score = savedInstanceState.getInt("currentScore")
            total = savedInstanceState.getInt("currentTotal")
        }
        setContentView(R.layout.activity_main)

        gameSpace = findViewById(R.id.game_space)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        resetButton = findViewById(R.id.reset_button)
        scoreTextView = findViewById(R.id.score_textview)
        scoreTextView.text = getString(R.string.score_label, score, total)

        startButton.setOnClickListener { startGame() }
        stopButton.setOnClickListener { stopGame() }
        resetButton.setOnClickListener { resetGame() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.falling_stars_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.help -> {
                // Launch the HelpActivity
                val intent = Intent(this, Help::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun startGame() {
        if (!isGameRunning) {
            startTime = System.currentTimeMillis()
            isGameRunning = true

            startButton.isEnabled = false
            stopButton.isEnabled = true

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
        //fallingShape.setImageResource(R.drawable.triangle)
        fallingShape.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        /* YOUR STEP 2 CODE HERE */
        val random = Random.Default
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        fallingShape.setColorFilter(Color.rgb(red, green, blue))

        fallingShape.setOnTouchListener { v, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameSpace.removeView(fallingShape)
                    score += 1
                    scoreTextView.text = getString(R.string.score_label, score, total)
                }
            }
            true
        }

        gameSpace.addView(fallingShape)

        /* YOUR STEP 3 CODE HERE */
        val maxX = gameSpace.width - fallingShape.drawable.intrinsicWidth
        val x = random.nextInt(maxX)

        fallingShape.x = x.toFloat()

        val y = gameSpace.height.toFloat()
        val randomDuration = random.nextInt(5000 - 1000) + 1000

        var fallDot = ObjectAnimator.ofFloat(fallingShape, "translationY", 0f, y)
        fallDot.interpolator = LinearInterpolator()
        fallDot.duration = randomDuration.toLong()
        fallDot.resume()
        fallDot.start()

        total += 1

        fallDot.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                scoreTextView.text = getString(R.string.score_label, score, total)
            }
            override fun onAnimationEnd(animation: Animator) {
                gameSpace.removeView(fallingShape)
            }
        })

    }

    private fun stopGame() {
        isGameRunning = false
        stopTime = System.currentTimeMillis()
        startButton.isEnabled = true
        stopButton.isEnabled = false

        scoreTextView.text = getString(R.string.score_label, score, total)

        val pauseDuration = abs(startTime - stopTime)

        for (i in 0 until gameSpace.childCount) {
            val childView = gameSpace.getChildAt(i)
            // Add a listener to each child view
            ObjectAnimator.ofFloat(childView, View.Y, childView.y, childView.y)
                .apply {
                    duration = pauseDuration // Set the pause duration
                    start()
                }
        }
    }

    private fun resetGame() {
        isGameRunning = false
        startButton.isEnabled = true
        stopButton.isEnabled = true
        score = 0
        total = 0
        scoreTextView.text = getString(R.string.score_label, score, total)
        gameSpace.removeAllViews()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("currentScore", score)
        savedInstanceState.putInt("currentTotal", total)
    }
}
