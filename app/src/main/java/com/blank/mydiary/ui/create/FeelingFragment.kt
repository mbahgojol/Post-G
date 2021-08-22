package com.blank.mydiary.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blank.mydiary.R
import com.blank.mydiary.databinding.FragmentFeelingBinding
import java.text.SimpleDateFormat
import java.util.*

class FeelingFragment : Fragment() {
    private lateinit var binding: FragmentFeelingBinding

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

        val formatter = SimpleDateFormat("EEE, dd MMM yyyy")
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
            val action = FeelingFragmentDirections.actionFeelingFragmentToTitleFragment(feeling)
            findNavController().navigate(action)
        }

        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }
    }
}


fun View.select() {
    setBackgroundResource(R.drawable.bg_color_merahmuda)
}

fun View.unselect() {
    setBackgroundResource(0)
}