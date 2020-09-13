package org.poul.bits.wearos.complications

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import android.text.format.DateUtils
import org.poul.bits.android.lib.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.lib.model.enum.BitsStatus
import org.poul.bits.wearos.MainActivity
import org.poul.bits.wearos.R

class PoulBitsComplicationProviderService : ComplicationProviderService() {
    override fun onComplicationUpdate(
        complicationId: Int, type: Int, manager: ComplicationManager
    ) {
        val bitsData = SharedPrefsWidgetStorageHelper(this).bitsData

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            complicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val iconRes = when (bitsData.status) {
            BitsStatus.OPEN -> R.drawable.ic_door_open
            BitsStatus.CLOSED -> R.drawable.ic_door_closed
            BitsStatus.UNKNOWN, null -> R.drawable.ic_door_unknown
        }

        val shortText = ComplicationText.plainText(
            when (bitsData.status) {
                BitsStatus.OPEN          -> getString(R.string.complication_open)
                BitsStatus.CLOSED        -> getString(R.string.complication_closed)
                BitsStatus.UNKNOWN, null -> getString(R.string.complication_gialla)
            }
        )

        val statusChangeTime = DateUtils.getRelativeTimeSpanString(
            bitsData.lastModified!!.time,
            System.currentTimeMillis(),
            0L,
            DateUtils.FORMAT_ABBREV_ALL
        ) as String

        val longText = ComplicationText.plainText(
            when (bitsData.status) {
                BitsStatus.OPEN          -> getString(
                    R.string.complication_opened_at,
                    statusChangeTime
                )
                BitsStatus.CLOSED        -> getString(
                    R.string.complication_closed_at,
                    statusChangeTime
                )
                BitsStatus.UNKNOWN, null -> getString(R.string.headquarters_gialla)
            }
        )

        // Build ComplicationData
        when (type) {
            ComplicationData.TYPE_SHORT_TEXT ->
                ComplicationData.Builder(type)
                    .setShortText(shortText)
            ComplicationData.TYPE_LONG_TEXT ->
                ComplicationData.Builder(type)
                    .setLongText(longText)
            ComplicationData.TYPE_ICON ->
                ComplicationData.Builder(type)
            else                             ->
                null
        }
            ?.setIcon(Icon.createWithResource(this, iconRes))
            ?.setTapAction(pendingIntent)
            ?.build()
            ?.let { manager.updateComplicationData(complicationId, it) }
            ?: manager.noUpdateRequired(complicationId)
    }
}