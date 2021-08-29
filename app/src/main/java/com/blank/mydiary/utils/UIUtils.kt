package com.blank.mydiary.utils

import android.content.Context
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blank.mydiary.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.File
import kotlin.math.roundToInt


fun ImageView.getFeeling(code: Int) {
    val emotes = listOf(
        R.drawable.smiling_face_with_heart_eyes,
        R.drawable.grinning_squinting_face,
        R.drawable.neutral_face,
        R.drawable.pensive_face,
        R.drawable.face_with_steam_from_nose,
    )
    setImageResource(emotes[code])
}

fun TextView.getFeelingStatus(code: Int) {
    val emotes = listOf(
        "Loved",
        "Happy",
        "Neutral",
        "Sad",
        "Angry",
    )
    text = emotes[code]
}

fun Context.getDeviceId(): String = Settings.Secure.getString(
    contentResolver,
    Settings.Secure.ANDROID_ID
)

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

fun View.hide() {
    visibility = GONE
}

fun View.show() {
    visibility = VISIBLE
}

fun getDuration(file: File): String {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(file.absolutePath)
    val durationStr =
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    return formateMilliSeccond(durationStr?.toLong()!!)
}

fun getDuration(url: String): String {
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(url)
        val durationStr =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        formateMilliSeccond(durationStr?.toLong()!!)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        "00:00"
    }
}

fun formateMilliSeccond(milliseconds: Long): String {
    var finalTimerString = ""
    var secondsString = ""

    // Convert total duration into time
    val hours = (milliseconds / (1000 * 60 * 60)).toInt()
    val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

    // Add hours if there
    if (hours > 0) {
        finalTimerString = "$hours:"
    }

    // Prepending 0 to seconds if it is one digit
    secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        "" + seconds
    }
    finalTimerString = "$finalTimerString$minutes:$secondsString"
    return finalTimerString
}

fun getColorSave(id: Int): Int {
    val colors = mutableListOf(
        R.color.white,
        R.color.biruJurnal,
        R.color.kuningJurnal,
        R.color.pinkJurnal
    )
    return colors[id]
}