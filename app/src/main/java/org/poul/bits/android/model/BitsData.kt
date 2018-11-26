package org.poul.bits.android.model

import android.os.Parcel
import eu.depau.commons.android.kotlin.KParcelable
import eu.depau.commons.android.kotlin.parcelableCreator
import org.poul.bits.android.model.enum.BitsStatus
import java.util.*

data class BitsData(
    val status: BitsStatus?,
    val modifiedBy: String?,
    val lastModified: Date?,
    val sensors: List<BitsSensorData>?,
    val message: BitsMessage?,
    val sensorsHistory: List<BitsSensorData>?
) : KParcelable {
    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        status = parcel.readSerializable() as BitsStatus,
        modifiedBy = parcel.readString()!!,
        lastModified = parcel.readSerializable() as Date,
        sensors = parcel.readParcelableArray(BitsSensorData::class.java.classLoader)!!.map { it as BitsSensorData },
        message = parcel.readParcelable<BitsMessage>(BitsMessage::class.java.classLoader)!!,
        sensorsHistory = parcel.readParcelableArray(BitsSensorData::class.java.classLoader)!!.map { it as BitsSensorData }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(status)
        parcel.writeString(modifiedBy)
        parcel.writeSerializable(lastModified)
        parcel.writeParcelableArray(sensors?.toTypedArray(), flags)
        parcel.writeParcelable(message, flags)
        parcel.writeParcelableArray(sensorsHistory?.toTypedArray(), flags)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsData)
    }
}