package com.example.gameapp.SOLO.BoatGame

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.view.View
import com.example.gameapp.R
import kotlin.random.Random

data class Obstacle(var x: Float, var y: Float, val width: Float, val height: Float, val bitmap: Bitmap)

class BoatGameView(context: Context) : View(context), SensorEventListener {

    private var boatBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.boat)
    private var boatX = 0f
    private var boatY = 0f
    private var boatWidth = 0f
    private var boatHeight = 0f

    private var riverBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.riviere)
    private var sensorX = 0f

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val obstacles = mutableListOf<Obstacle>()
    private var lives = 3
    private val obstacleSpeed = 5f

    private var gameOver = false
    private var gameOverSound: MediaPlayer? = null
    private var gameOverPlayed = false

    init {
        post {
            val scaleFactor = 0.2f
            boatWidth = width * scaleFactor
            if (boatWidth == 0f) boatWidth = 150f
            boatHeight = boatBitmap.height * (boatWidth / boatBitmap.width)
            boatBitmap = Bitmap.createScaledBitmap(boatBitmap, boatWidth.toInt(), boatHeight.toInt(), true)

            boatX = (width - boatWidth) / 2
            boatY = height - boatHeight - 50f
        }

        generateObstacles()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun startGame() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        post(updateRunnable)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!gameOver) {
                update()
                invalidate()
                postDelayed(this, 16)
            }
        }
    }

    private fun update() {
        if (width == 0) return

        boatX -= sensorX * 10
        boatX = boatX.coerceIn(0f, width - boatWidth)

        for (obstacle in obstacles) {
            obstacle.y += obstacleSpeed
        }

        obstacles.removeAll { it.y > height }

        checkCollisions()

        if (Random.nextInt(100) < 2 && !gameOver) {
            generateObstacles()
        }
    }

    private fun generateObstacles() {
        val obstacleBitmap = BitmapFactory.decodeResource(resources, R.drawable.obstacle)

        val scaleFactor = 0.1f
        val obstacleWidth = obstacleBitmap.width * scaleFactor
        val obstacleHeight = obstacleBitmap.height * scaleFactor

        val scaledObstacleBitmap = Bitmap.createScaledBitmap(obstacleBitmap, obstacleWidth.toInt(), obstacleHeight.toInt(), true)

        val randomX = Random.nextFloat() * (width - obstacleWidth)
        val randomY = -obstacleHeight

        val obstacle = Obstacle(randomX, randomY, obstacleWidth, obstacleHeight, scaledObstacleBitmap)
        obstacles.add(obstacle)
    }

    private fun checkCollisions() {
        for (obstacle in obstacles) {
            if (boatX < obstacle.x + obstacle.width &&
                boatX + boatWidth > obstacle.x &&
                boatY < obstacle.y + obstacle.height &&
                boatY + boatHeight > obstacle.y) {

                lives -= 1
                obstacles.clear()
                break
            }
        }

        if (lives <= 0 && !gameOverPlayed) {
            gameOver = true
            gameOverPlayed = true
            gameOverSound = MediaPlayer.create(context, R.raw.mauvaise_reponse)
            gameOverSound?.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        riverBitmap = Bitmap.createScaledBitmap(riverBitmap, width, height, true)
        canvas.drawBitmap(riverBitmap, 0f, 0f, null)
        canvas.drawBitmap(boatBitmap, boatX, boatY, null)

        for (obstacle in obstacles) {
            canvas.drawBitmap(obstacle.bitmap, obstacle.x, obstacle.y, null)
        }

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 50f
        }
        canvas.drawText("Vies : $lives", 20f, 60f, paint)

        if (gameOver) {
            val gameOverPaint = Paint().apply {
                color = Color.BLACK
                textSize = 100f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Game Over", width / 2f, height / 2f, gameOverPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
        gameOverSound?.release()
        gameOverSound = null
    }
}
