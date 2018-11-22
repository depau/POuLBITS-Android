package eu.depau.commons.android.kotlin.ktexts

import android.content.Context
import android.os.Build
import android.util.TypedValue


fun Context.getAccentColor(): Int {
    val colorAttr = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
            android.R.attr.colorAccent
        else ->
            resources.getIdentifier("colorAccent", "attr", packageName)
    }

    val outValue = TypedValue()
    theme.resolveAttribute(colorAttr, outValue, true)
    return outValue.data
}