package com.blank.mydiary.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.mydiary.R
import com.blank.mydiary.api.FirebaseService
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.data.MyJurnal
import com.blank.mydiary.databinding.ActivityHomeBinding
import com.blank.mydiary.service.AlarmReceiver
import com.blank.mydiary.ui.create.CreateJurnalActivity
import com.blank.mydiary.ui.edit.EditActivity
import com.blank.mydiary.ui.search.SearchActivity
import com.blank.mydiary.utils.VerticalSpaceItemDecoration
import com.blank.mydiary.utils.dp
import com.blank.mydiary.utils.getDeviceId
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.toObjects
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private val formatter = SimpleDateFormat("EEE, dd MMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alarmReceiver = AlarmReceiver()
        if (!alarmReceiver.isAlarmOn(this))
            alarmReceiver.setRepeatingAlarm(this)

        val date = Date()
        binding.content.tvToday.text = formatter.format(date)
        binding.content.currentDate.text = formatter.format(date)

        mutableListOf(
            binding.content.btnArrowLeft,
            binding.content.btnArrowRight,
            binding.content.currentDate
        ).forEach {
            it.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(this, this, year, month, day)
                    .show()
            }
        }

        binding.bottomNavView.menu.getItem(1).isEnabled = false
        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.icalendar -> {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(this, this, year, month, day)
                        .show()
                }
                R.id.isearch -> {
                    Intent(this, SearchActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            Intent(this, CreateJurnalActivity::class.java).apply {
                startActivity(this)
            }
        }

        mutableListOf(
            binding.content.cardMood,
            binding.content.listEmote,
            binding.content.btnLove,
            binding.content.btnHappy,
            binding.content.btnNeutral,
            binding.content.btnSad,
            binding.content.btnAngry
        ).forEach {
            it.setOnClickListener {
                Intent(this, CreateJurnalActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        binding.content.rvJurnal.layoutManager = LinearLayoutManager(this)
        binding.content.rvJurnal.addItemDecoration(VerticalSpaceItemDecoration(16.dp))

        viewModel.resultStateDeleteJurnal.observe(this) {
            when (it) {
                is ResultState.Error -> {
                    FirebaseCrashlytics.getInstance().recordException(it.e)
                    Log.e("Delete Data", it.e.message.toString())
                }
                is ResultState.Success<*> -> {
                    Toast.makeText(this, "Delete jurnal berhasil", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {

                }
            }
        }

        binding.content.pbhome.isVisible = true
        fetchData(binding.content.tvToday.text.toString())
    }

    private fun fetchData(date: String) {
        FirebaseService.getText(getDeviceId())
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val model = snapshot.toObjects<Jurnal>().toMutableList()
                    val adapter = HomeAdapter(model)
                    adapter.setClickListener {
                        val jurnal = MyJurnal(
                            it.background,
                            it.date,
                            it.feeling,
                            it.fileName,
                            it.msg,
                            it.title,
                        )
                        Intent(this, EditActivity::class.java).apply {
                            putExtra("jurnal", jurnal)
                            startActivity(this)
                        }
                    }
                    binding.content.rvJurnal.adapter = adapter
                    adapter.setClickDelete(viewModel::deleteJurnal)
                } else {
                    val adapter = HomeAdapter(mutableListOf())
                    binding.content.rvJurnal.adapter = adapter
                }

                binding.content.pbhome.isVisible = false
            }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val dateString = formatter.format(mCalendar.time)
        binding.content.currentDate.text = dateString
        fetchData(dateString)
    }
}