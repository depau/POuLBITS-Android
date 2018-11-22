package eu.depau.commons.android.kotlin.ktexts

private fun <T> toHexColor(intColor: T): String where T: Number =
    String.format("#%06X", 0xFFFFFFL and intColor.toLong())

fun Long.toHexColor() = toHexColor(this)
fun Int.toHexColor() = toHexColor(this)