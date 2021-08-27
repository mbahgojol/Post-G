package com.blank.mydiary.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blank.mydiary.R
import com.blank.mydiary.data.Jurnal
import com.blank.mydiary.databinding.ItemJurnalBinding
import com.blank.mydiary.databinding.NoJurnalLayoutBinding
import com.blank.mydiary.utils.getColorSave
import com.blank.mydiary.utils.getFeeling
import com.blank.mydiary.utils.getFeelingStatus
import com.chauthai.swipereveallayout.ViewBinderHelper


class HomeAdapter(private val data: MutableList<Jurnal>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: (Jurnal) -> Unit
    private lateinit var delete: (Jurnal) -> Unit
    private val binderHelper = ViewBinderHelper()
    var isSearchPage = false

    fun setClickListener(listener: (Jurnal) -> Unit) {
        this.listener = listener
    }

    fun setClickDelete(delete: (Jurnal) -> Unit) {
        this.delete = delete
    }

    private fun deleteJurnal(position: Int) {
        this.data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> {
                NoJurnalViewHolder(
                    NoJurnalLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                HomeViewHolder(
                    ItemJurnalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeViewHolder) {
            val item = data[position]
            holder.bind(item)
            holder.v.frontLayout.setOnClickListener {
                listener(item)
            }
            binderHelper.bind(holder.v.swipeLayout, item.title)
            holder.v.deleteLayout.setOnClickListener {
                deleteJurnal(position)
                delete.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = if (data.isEmpty()) 1 else data.size

    override fun getItemViewType(position: Int): Int {
        return if (data.isEmpty()) {
            0
        } else {
            1
        }
    }

    inner class NoJurnalViewHolder(v: NoJurnalLayoutBinding) :
        RecyclerView.ViewHolder(v.root) {
        init {
            if (isSearchPage) {
                v.wordingStatus.text = "Jurnal not found!"
            } else {
                v.wordingStatus.text = v.root.context.getString(R.string.no_journal_for_this_day)
            }
        }
    }
}

class HomeViewHolder(val v: ItemJurnalBinding) : RecyclerView.ViewHolder(v.root) {
    fun bind(jurnal: Jurnal) {
        v.bgColor.setBackgroundColor(
            ContextCompat.getColor(
                v.root.context,
                if (jurnal.background == 0) R.color.merahmudaJurna else
                    getColorSave(jurnal.background)
            )
        )
        v.ivFeeling.getFeeling(jurnal.feeling ?: 0)
        v.tvTitle.text = jurnal.title
        v.tvStatusFeeling.getFeelingStatus(jurnal.feeling ?: 0)
    }
}