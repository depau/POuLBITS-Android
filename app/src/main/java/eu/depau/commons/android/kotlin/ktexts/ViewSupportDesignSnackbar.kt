package eu.depau.commons.android.kotlin.ktexts

import android.support.design.widget.Snackbar
import android.view.View

fun View.snackbar(message: CharSequence, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}