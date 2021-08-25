package com.blank.mydiary.ui.create

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blank.mydiary.R
import com.blank.mydiary.databinding.FragmentFeelingBinding
import java.text.SimpleDateFormat
import java.util.*

class FeelingFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: FragmentFeelingBinding
    private val formatter = SimpleDateFormat("EEE, dd MMM yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeelingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = Date()
        binding.currentDate.text = formatter.format(date)

        var feeling = 1
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

        binding.btnNext.setOnClickListener {
            val action = FeelingFragmentDirections.actionFeelingFragmentToTitleFragment(
                feeling,
                binding.currentDate.text.toString()
            )
            findNavController().navigate(action)
        }

        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        mutableListOf(binding.btnIconCalendar, binding.currentDate)
            .forEach {
                it.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(requireActivity(), this, year, month, day)
                        .show()
                }
            }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        binding.currentDate.text = formatter.format(mCalendar.time)
    }
}


fun View.select() {
    setBackgroundResource(R.drawable.bg_color_merahmuda)
}

fun View.unselect() {
    setBackgroundResource(0)
}