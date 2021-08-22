package com.blank.mydiary.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = margin
        outRect.left = margin
        outRect.right = margin
        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = 50.dp
        }
    }
}