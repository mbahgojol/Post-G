package com.blank.mydiary.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.databinding.ItemJurnalBinding
import com.blank.mydiary.utils.getFeeling
import com.blank.mydiary.utils.getFeelingStatus

class HomeAdapter(private val data: MutableList<Jurnal>) : RecyclerView.Adapter<HomeViewHolder>() {
    private lateinit var listener: (Jurnal) -> Unit

    fun setClickListener(listener: (Jurnal) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder =
        HomeViewHolder(
            ItemJurnalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = data.size
}

class HomeViewHolder(private val v: ItemJurnalBinding) : RecyclerView.ViewHolder(v.root) {
    fun bind(jurnal: Jurnal) {
        v.ivFeeling.getFeeling(jurnal.feeling ?: 0)
        v.tvTitle.text = jurnal.title
        v.tvStatusFeeling.getFeelingStatus(jurnal.feeling ?: 0)
    }
}