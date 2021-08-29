package com.blank.mydiary.ui.edit

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.blank.mydiary.databinding.ItemAudioBinding
import com.blank.mydiary.utils.formateMilliSeccond
import com.blank.mydiary.utils.getDuration
import java.io.File
import java.io.IOException

class RecordAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val recyclerView: RecyclerView,
    private val online: Boolean
) :
    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    private val data = mutableListOf<String>()
    private var listenerDelete: ((Int, String) -> Unit?)? = null

    fun listenerDelete(listenerDelete: (Int, String) -> Unit) {
        this.listenerDelete = listenerDelete
    }

    fun addData(data: String) {
        this.data.add(data)
        notifyDataSetChanged()
    }

    fun setData(data: MutableList<String>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    private fun deleteRecord(index: Int) {
        this.data.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, data.size)
    }

    fun getData() = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder =
        RecordViewHolder(
            ItemAudioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        lifecycleOwner.lifecycle.addObserver(holder)
        holder.onBind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    inner class RecordViewHolder(val v: ItemAudioBinding) : RecyclerView.ViewHolder(v.root),
        LifecycleObserver {

        private var player: MediaPlayer? = null
        private var mStartPlaying = true
        private val handler = Handler(Looper.getMainLooper())
        private var fileName = ""
        private var currentPosition = -1

        fun onBind(fileName: String, position: Int) {
            this.fileName = fileName
            v.tvTitleAudio.text = "Audio ${position.plus(1)}"

            if (online && !fileName.contains(v.root.context.packageName)) {
                v.endTime.text = getDuration(fileName)
            } else {
                v.endTime.text = getDuration(File(fileName))
            }

            v.btnCloseAudio.setOnClickListener {
                deleteRecord(position)
                listenerDelete?.invoke(position, fileName)
            }

            v.play.setOnClickListener {
                v.play.changeImg()

                if (currentPosition >= 0 && currentPosition != position) {
                    val holder =
                        recyclerView.findViewHolderForLayoutPosition(currentPosition) as RecordViewHolder
                    holder.v.play.changeImg()
                }

                if (currentPosition == position) {
                    onPlay(mStartPlaying)
                    mStartPlaying = !mStartPlaying
                } else {
                    onPlay(false)
                    mStartPlaying = true
                    onPlay(mStartPlaying)
                    mStartPlaying = !mStartPlaying
                }

                currentPosition = position
            }

            v.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
        }

        private fun onPlay(start: Boolean) = if (start) {
            handler.postDelayed(updateSongTime, 1000)
            startPlaying()
        } else {
            handler.removeCallbacks(updateSongTime)
            stopPlaying()
        }

        private fun startPlaying() {
            player = MediaPlayer().apply {
                try {
                    setDataSource(fileName)
                    prepare()
                    start()
                } catch (e: IOException) {
                    Log.e("Audio Adapter", "prepare() failed")
                }
            }

            v.seekbar.max = player?.duration ?: 0
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun stop() {
            player?.release()
            player = null
            handler.removeCallbacks(updateSongTime)
            if (!mStartPlaying) {
                v.play.changeImg()
                mStartPlaying = !mStartPlaying
            }
        }

        private var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = player?.currentPosition ?: 0
                v.startTime.text = formateMilliSeccond(getCurrent.toLong())
                v.seekbar.progress = getCurrent
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1000)

                if (v.endTime.text.toString() == v.startTime.text.toString()) {
                    v.seekbar.progress = 0
                    mStartPlaying = !mStartPlaying
                    handler.removeCallbacks(this)
                    v.startTime.text = formateMilliSeccond(0)
                    v.play.changeImg()
                }
            }
        }

        private fun stopPlaying() {
            player?.release()
            player = null
        }
    }
}