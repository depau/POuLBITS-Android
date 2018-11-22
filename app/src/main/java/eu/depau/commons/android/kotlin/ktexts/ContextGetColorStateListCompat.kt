package eu.depau.commons.android.kotlin.ktexts

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build

@Suppress("DEPRECATION")
fun Resources.getColorStateListCompat(id: Int, theme: Resources.Theme): ColorStateList {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> this.getColorStateList(id, theme)
        else -> this.getColorStateList(id)
    }
}