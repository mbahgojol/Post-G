package com.blank.mydiary.utils

import android.content.Context
import android.graphics.Typeface
import java.lang.reflect.Field

object TypefaceUtil {
    fun overrideFont(
        context: Context
    ) {
        try {
            val customFontTypeface =
                Typeface.createFromAsset(context.assets, "fonts/nunito.ttf")
            val defaultFontTypefaceField: Field =
                Typeface::class.java.getDeclaredField("SERIF")
            defaultFontTypefaceField.isAccessible = true
            defaultFontTypefaceField.set(null, customFontTypeface)
        } catch (e: Exception) {
        }
    }
}