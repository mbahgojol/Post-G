package com.blank.mydiary.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyJurnal(
    val background: String = "",
    val date: String = "",
    val feeling: Int = 1,
    val fileName: MutableList<String> = mutableListOf(),
    val msg: String = "",
    val title: String = ""
) : Parcelable