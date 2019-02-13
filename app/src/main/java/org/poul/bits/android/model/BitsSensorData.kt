package org.poul.bits.android.model

import android.os.Parcel
import eu.depau.kotlet.android.parcelable.KotletParcelable
import eu.depau.kotlet.android.parcelable.parcelableCreator
import org.poul.bits.android.model.enum.BitsSensorType
import java.util.*

data class BitsSensorData(
    val value: Double,
    val sensorId: Long,
    val modifiedBy: String,
    val lastModified: Date,
    val type: BitsSensorType?
) : KotletParcelable {
    constructor(parcel: Parcel) : this(
        value = parcel.readDouble(),
        sensorId = parcel.readLong(),
        modifiedBy = parcel.readString()!!,
        lastModified = parcel.readSerializable() as Date,
        type = parcel.readSerializable() as BitsSensorType
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(value)
        parcel.writeLong(sensorId)
        parcel.writeString(modifiedBy)
        parcel.writeSerializable(lastModified)
        parcel.writeSerializable(type)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsSensorData)
    }
}