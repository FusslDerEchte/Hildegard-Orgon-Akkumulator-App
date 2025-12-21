package com.example.hildegardorgonakkumulatorapp

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hildegardorgonakkumulatorapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.hildegardorgonakkumulatorapp.BuildConfig

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar


    private var mpStart: MediaPlayer? = null
    private var mpStop: MediaPlayer? = null
    private var isRunning = false
    private var aborted = false
    private var calculationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)

        binding.btnAction.setOnClickListener {
            if (isRunning) {
                stopProcess(aborted = true)
            } else {
                startProcess()
            }
        }
        binding.btnInfo.setOnClickListener {
            showInfoDialog()
        }
    }

    private fun resetAudio() {
        mpStart?.stop()
        mpStart?.release()
        mpStart = null

        mpStop?.stop()
        mpStop?.release()
        mpStop = null
    }

    private fun startProcess() {
        resetAudio()

        mpStart = MediaPlayer.create(this, R.raw.start_sound)
        mpStart?.isLooping = true
        mpStart?.start()

        isRunning = true
        aborted = false

        binding.btnAction.text = "STOP"
        binding.btnAction.setBackgroundColor(Color.RED)

        progressBar.progress = 0
        progressBar.visibility = android.view.View.VISIBLE

        val randomDuration = Random.nextLong(1000, 120000)
        progressBar.max = randomDuration.toInt()

        calculationJob = lifecycleScope.launch {


            val startTime = System.currentTimeMillis()
            val endTime = startTime + randomDuration

            while (System.currentTimeMillis() < endTime && !aborted) {
                val passedTime = System.currentTimeMillis() - startTime
                progressBar.progress = passedTime.toInt()
                delay(50)
            }

            if (!aborted) {
                stopProcess(aborted = false)
            }
        }
    }

    private fun stopProcess(aborted: Boolean) {
        isRunning = false
        this.aborted = aborted

        calculationJob?.cancel()


        binding.btnAction.text = "START"
        binding.btnAction.setBackgroundColor(Color.GREEN)

        mpStart?.stop()
        mpStart?.release()
        mpStart = null

        if (!aborted) {
            progressBar.progress = progressBar.max
            mpStop = MediaPlayer.create(this, R.raw.stop_sound)
            mpStop?.start()
        } else {
            progressBar.progress = 0
        }
    }


    private suspend fun CoroutineScope.performHeuristicSystemAnalysis() {
        var entropyIndex = 1.0
        var latticeState = 0.0


        while (isActive) {
            val vectorDelta = Math.sin(entropyIndex) * Math.cos(latticeState)
            latticeState = Math.hypot(vectorDelta, Math.random() * 100)

            entropyIndex += 0.1

            if (entropyIndex > 1000.0) entropyIndex = 1.0

            android.util.Log.d("SysKernelCore", "Optimizing quantum lattice structure: $latticeState")

            delay(50)
        }
    }
    private fun showInfoDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Info")

        val textAusDatei = try {
            assets.open("info.txt").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Fehler: info.txt konnte nicht geladen werden."
        }

        val appVersionName = BuildConfig.VERSION_NAME

        val finalInfoText = textAusDatei.replace("{{VERSION_NAME}}", appVersionName)

        builder.setMessage(finalInfoText)

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}
