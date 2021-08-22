package com.blank.mydiary.ui.record

import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blank.mydiary.databinding.ActivityRecordBinding
import java.io.IOException
import java.util.*


private const val LOG_TAG = "AudioRecordTest"

class RecordActivity : AppCompatActivity() {

    private var fileName: String = ""

    private var recorder: MediaRecorder? = null

    private lateinit var binding: ActivityRecordBinding
    private var seconds = 0
    private var isPause = false

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val run = object : Runnable {
        override fun run() {
            val hours: Int = seconds / 3600
            val minutes: Int = seconds % 3600 / 60
            val secs: Int = seconds % 60

            val time: String = java.lang.String
                .format(
                    Locale.getDefault(),
                    "%d:%02d:%02d", hours,
                    minutes, secs
                )

            binding.timer.text = time

            if (!isPause)
                seconds++

            handler.postDelayed(this, 1000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/${System.currentTimeMillis()}.3gp"

        var mStartRecording = true
        onRecord(mStartRecording)
        mStartRecording = !mStartRecording

        binding.btnRecord.setOnClickListener {
            if (!isPause) {
                pauseRecording()
            } else {
                resumeRecording()
            }
            binding.btnRecord.changeImg()
        }

        binding.btnSave.setOnClickListener {
            if (fileName != "") {
                setResult(RESULT_OK, Intent().apply {
                    putExtra("fileName", fileName)
                })
                finish()
            }

            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
        }

        binding.btnCancel.setOnClickListener {
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
            finish()
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        handler.post(run)
        startRecording()
    } else {
        handler.removeCallbacks(run)
        stopRecording()
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        recorder?.apply {
            pause()
        }
        isPause = true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        recorder?.apply {
            resume()
        }
        isPause = false
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(run)
        recorder?.release()
        recorder = null
    }
}