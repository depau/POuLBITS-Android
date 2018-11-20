package org.poul.bits.android.model

import android.os.Parcel
import eu.depau.commons.android.kotlin.KParcelable
import eu.depau.commons.android.kotlin.parcelableCreator
import java.util.*

data class BitsTemperatureData(
    val value: Double,
    val sensorId: Long,
    val modifiedBy: String,
    val lastModified: Date
) : KParcelable {
    constructor(parcel: Parcel) : this(
        value = parcel.readDouble(),
        sensorId = parcel.readLong(),
        modifiedBy = parcel.readString()!!,
        lastModified = parcel.readSerializable() as Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(value)
        parcel.writeLong(sensorId)
        parcel.writeString(modifiedBy)
        parcel.writeSerializable(lastModified)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsTemperatureData)
    }
}