package com.blank.mydiary.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blank.mydiary.R
import com.blank.mydiary.databinding.BottomsheetBgLayoutBinding
import com.blank.mydiary.databinding.ItemBgLayoutBinding
import com.blank.mydiary.utils.HorizontalSpaceItemDecoration
import com.blank.mydiary.utils.dp
import com.blank.mydiary.utils.hide
import com.blank.mydiary.utils.show
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ColorPickerBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetBgLayoutBinding
    private lateinit var onClickColor: (Int) -> Unit

    fun setClickListener(onClickColor: (Int) -> Unit) {
        this.onClickColor = onClickColor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetBgLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colors = mutableListOf(
            R.drawable.bg_item,
            R.drawable.bg_item_biru,
            R.drawable.bg_item_kuning,
            R.drawable.bg_item_pink
        )
        val adapter = ColorsAdapter(colors)
        binding.rvBg.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBg.addItemDecoration(HorizontalSpaceItemDecoration(16.dp))
        binding.rvBg.adapter = adapter

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCheck.setOnClickListener {
            dismiss()
        }
    }

    inner class ColorsAdapter(private val data: MutableList<Int>) :
        RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder =
            ColorsViewHolder(
                ItemBgLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

        override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
            holder.onBind(data[position])
            if (position == 0) {
                holder.v.tvDefault.show()
            } else {
                holder.v.tvDefault.hide()
            }
            holder.itemView.setOnClickListener {
                onClickColor(position)
            }
        }

        override fun getItemCount(): Int = data.size

        inner class ColorsViewHolder(val v: ItemBgLayoutBinding) : RecyclerView.ViewHolder(v.root) {
            fun onBind(color: Int) {
                v.bgColor.setBackgroundResource(color)
            }
        }
    }
}