package org.poul.bits.android.misc

import android.content.Context
import android.text.Html
import androidx.core.content.ContextCompat
import eu.depau.kotlet.android.extensions.ui.context.accentColor
import eu.depau.kotlet.extensions.builtins.toHexColor

object SimpleHtml {
    val br = "<br>"

    fun esc(text: String) = Html.escapeHtml(text)

    fun bold(text: String) = "<b>$text</b>"
    fun b(text: String) = bold(text)

    fun italic(text: String) = "<i>$text</i>"
    fun i(text: String) = italic(text)

    fun strike(text: String) = "<strike>$text</strike>"
    fun s(text: String) = strike(text)

    fun u(text: String) = "<u>$text</u>"

    fun big(text: String) = "<big>$text</big>"
    fun small(text: String) = "<small>$text</small>"

    fun sup(text: String) = "<sup>$text</sup>"
    fun sub(text: String) = "<sub>$text</sub>"

    fun blockquote(text: String) = "<blockquote>$text</blockquote>"

    fun font(text: String, size: String? = null, color: String? = null, face: String? = null): String {
        val builder = StringBuilder("<font")

        if (size != null)
            builder.append(" size=\"$size\"")
        if (color != null)
            builder.append(" color=\"$color\"")
        if (face != null)
            builder.append(" face=\"$face\"")

        return builder.append(">").append(text).append("</font>").toString()
    }

    fun color(text: String, color: String) = font(text, color = color)
    fun color(context: Context, text: String, colorId: Int) =
        font(text, color = ContextCompat.getColor(context, colorId).toHexColor())
    fun size(text: String, size: String) = font(text, size = size)

    fun Context.accent(text: String) = color(text, accentColor.toHexColor())
}