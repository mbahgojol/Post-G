package com.blank.mydiary.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.mydiary.api.ResultState
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.data.MyJurnal
import com.blank.mydiary.databinding.ActivitySearchBinding
import com.blank.mydiary.ui.edit.EditActivity
import com.blank.mydiary.ui.home.HomeAdapter
import com.blank.mydiary.utils.VerticalSpaceItemDecoration
import com.blank.mydiary.utils.dp
import com.blank.mydiary.utils.getDeviceId
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.addItemDecoration(VerticalSpaceItemDecoration(16.dp))

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.etSearch.addTextChangedListener {
            binding.btnClear.isVisible = it.toString().isNotEmpty()
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            }
            false
        }

        binding.btnClear.setOnClickListener {
            binding.rvSearch.isVisible = false
            binding.wording.isVisible = false
            binding.etSearch.text.clear()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        viewModel.resultStateDeleteJurnal.observe(this) {
            when (it) {
                is ResultState.Error -> {
                    Log.e("Delete Data", it.e.message.toString())
                }
                is ResultState.Success<*> -> {
                    Toast.makeText(this, "Delete jurnal berhasil", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {

                }
            }
        }

        viewModel.resultStateSearch.observe(this) {
            when (it) {
                is ResultState.Error -> {
                    Toast.makeText(this, "${it.e.message}", Toast.LENGTH_SHORT).show()
                }

                is ResultState.Success<*> -> {
                    val snapshot = it.data as QuerySnapshot
                    if (snapshot != null && !snapshot.isEmpty) {
                        val model = snapshot.toObjects<Jurnal>().toMutableList()
                        val adapter = HomeAdapter(model)
                        adapter.setClickListener { jun ->
                            val jurnal = MyJurnal(
                                jun.background,
                                jun.date,
                                jun.feeling,
                                jun.fileName,
                                jun.msg,
                                jun.title,
                            )
                            Intent(this, EditActivity::class.java).apply {
                                putExtra("jurnal", jurnal)
                                startActivity(this)
                            }
                        }
                        binding.rvSearch.adapter = adapter
                        adapter.setClickDelete(viewModel::deleteJurnal)
                        binding.wording.isVisible = true
                    } else {
                        val adapter = HomeAdapter(mutableListOf())
                        adapter.isSearchPage = true
                        binding.rvSearch.adapter = adapter
                        binding.wording.isVisible = true
                    }

                    binding.pbSearch.isVisible = false
                }

                is ResultState.Loading -> {
                    binding.pbSearch.isVisible = it.loading
                }
            }
        }
    }

    private fun performSearch() {
        binding.rvSearch.isVisible = true
        viewModel.searchText(getDeviceId(), binding.etSearch.text.toString())
        binding.etSearch.clearFocus()
        val input: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}