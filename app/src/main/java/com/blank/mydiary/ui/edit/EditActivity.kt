package com.blank.mydiary.ui.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.MyJurnal
import com.blank.mydiary.data.SendJurnal
import com.blank.mydiary.databinding.ActivityEditBinding
import com.blank.mydiary.ui.create.select
import com.blank.mydiary.ui.create.unselect
import com.blank.mydiary.ui.record.RecordActivity
import com.blank.mydiary.utils.VerticalSpaceVersi2
import com.blank.mydiary.utils.dp
import com.blank.mydiary.utils.getDeviceId
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class EditActivity : AppCompatActivity() {

    private var fileName = MutableLiveData("")
    private lateinit var binding: ActivityEditBinding
    private val viewModel by viewModels<EditViewModel>()
    private var adapter: RecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutAudio.layoutManager = LinearLayoutManager(this)
        binding.layoutAudio.addItemDecoration(VerticalSpaceVersi2(16.dp))

        val android_id = getDeviceId()
        val title = intent.getStringExtra("title") ?: ""
        var feeling = intent.getIntExtra("feeling", 0)

        fileName.observe(this) {
            if (it != "") {
                adapter?.addData(it)
            }
        }

        val formatter = SimpleDateFormat("EEE, dd MMM yyyy")
        val date = Date()

        binding.etMsg.requestFocus()
        val emotes = listOf(
            binding.btnLove,
            binding.btnHappy,
            binding.btnNeutral,
            binding.btnSad,
            binding.btnAngry
        )

        emotes.forEachIndexed { index, lyt ->
            lyt.setOnClickListener {
                emotes.forEach { view -> view.unselect() }
                lyt.select()
                feeling = index
            }
        }

        if (intent.hasExtra("jurnal")) {
            adapter = RecordAdapter(this, binding.layoutAudio, true)
            binding.layoutAudio.adapter = adapter

            val jurnal = intent.getParcelableExtra<MyJurnal>("jurnal")!!
            binding.tvTitle.setText(jurnal.title)
            binding.etMsg.setText(jurnal.msg)
            emotes[jurnal.feeling].select()
            binding.date.text = jurnal.date
            viewModel.getAudio(jurnal.fileName)
        } else {
            adapter = RecordAdapter(this, binding.layoutAudio, false)
            binding.layoutAudio.adapter = adapter

            binding.date.text = formatter.format(date)
            emotes[feeling].select()
            binding.tvTitle.setText(title)
        }
        binding.btnRecord.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) -> {
                    resultLauncher.launch(Intent(this, RecordActivity::class.java))
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO
                    )
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val jurnal = SendJurnal(
                android_id,
                HashMap<String, Any>().apply {
                    adapter?.getData()?.let { it1 -> put("fileName", it1) }
                    put("msg", binding.etMsg.text.toString())
                    put("title", binding.tvTitle.text.toString())
                    put("feeling", feeling)
                    put("date", formatter.format(date))
                    put("background", "")
                }
            )
            viewModel.saveJurnal(jurnal)
        }

        viewModel.resultStatusSaveJurnal.observe(this) {
            when (it) {
                is ResultState.Success<*> -> {
                    Toast.makeText(this, "Berhasil simpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is ResultState.Error -> {
                    Toast.makeText(this, "${it.e.message}", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {
                    binding.progressbar.isVisible = it.loading
                }
            }
        }

        viewModel.resultStatusGetAudio.observe(this) {
            when (it) {
                is ResultState.Success<*> -> {
                    val data = it.data as MutableList<String>
                    adapter?.setData(data)
                }
                is ResultState.Error -> {
                    Toast.makeText(this, "${it.e.message}", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {
                    binding.progressbar.isVisible = it.loading
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                fileName.value = data?.getStringExtra("fileName").toString()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                resultLauncher.launch(Intent(this, RecordActivity::class.java))
            } else {
                Toast.makeText(this, "Anda tidak dapat merekam", Toast.LENGTH_SHORT).show()

            }
        }
}