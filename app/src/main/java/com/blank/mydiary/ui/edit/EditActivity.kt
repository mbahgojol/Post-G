package com.blank.mydiary.ui.edit

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
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
import com.blank.mydiary.utils.getColorSave
import com.blank.mydiary.utils.getDeviceId
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class EditActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var fileName = MutableLiveData("")
    private lateinit var binding: ActivityEditBinding
    private val viewModel by viewModels<EditViewModel>()
    private var adapter: RecordAdapter? = null
    private var bgcolor = 0
    private var isEdit = false
    private var myJurnal: MyJurnal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutAudio.layoutManager = LinearLayoutManager(this)
        binding.layoutAudio.addItemDecoration(VerticalSpaceVersi2(16.dp))

        val android_id = getDeviceId()
        val title = intent.getStringExtra("title") ?: ""
        val myDate = intent.getStringExtra("date") ?: ""
        var feeling = intent.getIntExtra("feeling", 0)

        fileName.observe(this) {
            if (it != "") {
                adapter?.addData(it)

                if (isEdit) {
                    myJurnal?.fileName?.add(it)
                }
            }
        }

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
            isEdit = true
            adapter = RecordAdapter(this, binding.layoutAudio, true)
            binding.layoutAudio.adapter = adapter
            adapter?.listenerDelete { posisi, file ->
                if (isEdit) {
                    run stop@{
                        myJurnal?.fileName?.forEachIndexed { index, s ->
                            if (file.contains(Regex("^.*(${s.split("/")[1]}).*$"))) {
                                myJurnal?.fileName?.removeAt(index)
                                return@stop
                            }
                        }
                    }
                }
            }

            myJurnal = intent.getParcelableExtra("jurnal") ?: MyJurnal()
            Log.e("MyJurnal", myJurnal.toString())

            binding.tvTitle.setText(myJurnal?.title)
            binding.etMsg.setText(myJurnal?.msg)
            feeling = myJurnal?.feeling ?: 1
            emotes[feeling].select()
            binding.date.text = myJurnal?.date
            bgcolor = myJurnal?.background ?: 0
            binding.bg.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    getColorSave(bgcolor)
                )
            )
            if (myJurnal?.fileName?.isNotEmpty() == true)
                viewModel.getAudio(myJurnal?.fileName ?: mutableListOf())
        } else {
            isEdit = false
            adapter = RecordAdapter(this, binding.layoutAudio, false)
            binding.layoutAudio.adapter = adapter

            binding.date.text = myDate
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
            if (isEdit) {
                val jurnal = SendJurnal(
                    android_id,
                    HashMap<String, Any>().apply {
                        put("fileName", myJurnal?.fileName ?: mutableListOf<String>())
                        put("msg", binding.etMsg.text.toString())
                        put("title", binding.tvTitle.text.toString())
                        put("feeling", feeling)
                        put("date", binding.date.text.toString())
                        put("background", bgcolor)
                        put("jurnalId", myJurnal?.jurnalId ?: UUID.randomUUID().toString())
                        put("deviceId", android_id)
                    }
                )
                viewModel.editJurnal(jurnal)
            } else {
                val jurnal = SendJurnal(
                    android_id,
                    HashMap<String, Any>().apply {
                        adapter?.getData()?.let { it1 -> put("fileName", it1) }
                        put("msg", binding.etMsg.text.toString())
                        put("title", binding.tvTitle.text.toString())
                        put("feeling", feeling)
                        put("date", binding.date.text.toString())
                        put("background", bgcolor)
                        put("jurnalId", UUID.randomUUID().toString())
                        put("deviceId", android_id)
                    }
                )
                viewModel.saveJurnal(jurnal)
            }
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

        binding.date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, this, year, month, day)
                .show()
        }

        binding.bgColor.setOnClickListener {
            val colorPickerBottomSheet = ColorPickerBottomSheet()
            colorPickerBottomSheet.show(supportFragmentManager, "")
            colorPickerBottomSheet.setClickListener { color ->
                bgcolor = color
                binding.bg.setBackgroundColor(ContextCompat.getColor(this, getColorSave(bgcolor)))
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy")
        binding.date.text = formatter.format(mCalendar.time)
    }
}