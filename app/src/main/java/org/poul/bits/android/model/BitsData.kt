package org.poul.bits.android.model

import android.os.Parcel
import eu.depau.commons.android.kotlin.KParcelable
import eu.depau.commons.android.kotlin.parcelableCreator
import org.poul.bits.android.model.enum.BitsStatus
import java.util.*

data class BitsData(
    val status: BitsStatus,
    val modifiedBy: String,
    val lastModified: Date,
    val temperature: BitsTemperatureData,
    val message: BitsMessage,
    val temperatureHistory: List<BitsTemperatureData>
) : KParcelable {
    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        status = parcel.readSerializable() as BitsStatus,
        modifiedBy = parcel.readString()!!,
        lastModified = parcel.readSerializable() as Date,
        temperature = parcel.readParcelable<BitsTemperatureData>(BitsTemperatureData::class.java.classLoader)!!,
        message = parcel.readParcelable<BitsMessage>(BitsMessage::class.java.classLoader)!!,
        temperatureHistory = parcel.readParcelableArray(BitsTemperatureData::class.java.classLoader)!!.map { it as BitsTemperatureData }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(status)
        parcel.writeString(modifiedBy)
        parcel.writeSerializable(lastModified)
        parcel.writeParcelable(temperature, flags)
        parcel.writeParcelable(message, flags)
        parcel.writeParcelableArray(temperatureHistory.toTypedArray(), flags)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsData)
    }
}