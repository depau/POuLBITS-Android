package org.poul.bits.android.lib.misc

import android.content.Context
import org.poul.bits.android.lib.R
import org.poul.bits.android.lib.model.enum.BitsStatus

fun Context.getTextForStatus(status: BitsStatus) = when (status) {
    BitsStatus.OPEN -> getString(R.string.headquarters_open)
    BitsStatus.CLOSED -> getString(R.string.headquarters_closed)
    BitsStatus.UNKNOWN -> getString(R.string.headquarters_gialla)
}

fun getColorForStatus(status: BitsStatus) = when (status) {
    BitsStatus.OPEN -> R.color.colorHQsOpen
    BitsStatus.CLOSED -> R.color.colorHQsClosed
    BitsStatus.UNKNOWN -> R.color.colorHQsGialla
}