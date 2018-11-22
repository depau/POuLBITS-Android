package org.poul.bits.android.misc

import android.content.Context
import org.poul.bits.R
import org.poul.bits.android.model.enum.BitsStatus

fun Context.getTextForStatus(status: BitsStatus) = when (status) {
    BitsStatus.OPEN -> getString(R.string.headquarters_open)
    BitsStatus.CLOSED -> getString(R.string.headquarters_closed)
}

fun getColorForStatus(status: BitsStatus) = when (status) {
    BitsStatus.OPEN -> R.color.colorHQsOpen
    BitsStatus.CLOSED -> R.color.colorHQsClosed
}