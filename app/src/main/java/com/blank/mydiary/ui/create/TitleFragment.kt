package com.blank.mydiary.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blank.mydiary.databinding.FragmentTitleBinding
import com.blank.mydiary.ui.edit.EditActivity


class TitleFragment : Fragment() {

    private lateinit var binding: FragmentTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args by navArgs<TitleFragmentArgs>()

        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            Intent(requireActivity(), EditActivity::class.java).apply {
                putExtra("feeling", args.feeling)
                putExtra("title", binding.etTitle.text.toString())
                putExtra("date", args.date)
                startActivity(this)
                requireActivity().finish()
            }
        }
    }
}