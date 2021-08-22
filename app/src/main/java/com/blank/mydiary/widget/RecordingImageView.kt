package com.blank.mydiary.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.blank.mydiary.R

class RecordingImageView : AppCompatImageView {

    private var imgPlay: Drawable? = null
    private var imgStop: Drawable? = null
    var isclicked = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.RecordingImageView, defStyle, 0
        )

        imgPlay = a.getDrawable(
            R.styleable.RecordingImageView_imgPlay
        )

        imgStop = a.getDrawable(
            R.styleable.RecordingImageView_imgStop
        )

        setImageDrawable(imgPlay)

        a.recycle()
    }

    fun changeImg() {
        isclicked = !isclicked
        if (isclicked) {
            setImageDrawable(imgStop)
        } else {
            setImageDrawable(imgPlay)
        }
    }
}