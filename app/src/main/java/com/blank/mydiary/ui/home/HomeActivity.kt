package com.blank.mydiary.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.mydiary.api.FirebaseService
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.data.MyJurnal
import com.blank.mydiary.databinding.ActivityHomeBinding
import com.blank.mydiary.ui.create.CreateJurnalActivity
import com.blank.mydiary.ui.edit.EditActivity
import com.blank.mydiary.utils.VerticalSpaceItemDecoration
import com.blank.mydiary.utils.dp
import com.blank.mydiary.utils.getDeviceId
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.toObjects
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val formatter = SimpleDateFormat("EEE, dd MMM yyyy")
        val date = Date()
        binding.content.tvToday.text = formatter.format(date)

        binding.bottomNavView.menu.getItem(1).isEnabled = false
        binding.fab.setOnClickListener {
            Intent(this, CreateJurnalActivity::class.java).apply {
                startActivity(this)
            }
        }

        mutableListOf(
            binding.content.btnLove,
            binding.content.btnHappy,
            binding.content.btnNeutral,
            binding.content.btnSad,
            binding.content.btnAngry
        ).forEach {
            it.setOnClickListener {
                Intent(this, CreateJurnalActivity::class.java)
            }
        }

        binding.content.rvJurnal.layoutManager = LinearLayoutManager(this)
        binding.content.rvJurnal.addItemDecoration(VerticalSpaceItemDecoration(16.dp))

        binding.content.pbhome.isVisible = true
        FirebaseService.getText(getDeviceId())
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
                } else {
                    val adapter = HomeAdapter(mutableListOf())
                    binding.content.rvJurnal.adapter = adapter
                }

                binding.content.pbhome.isVisible = false
            }
    }
}