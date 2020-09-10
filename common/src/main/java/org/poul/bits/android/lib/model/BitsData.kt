package org.poul.bits.android.lib.model

import android.os.Parcel
import eu.depau.kotlet.android.parcelable.KotletParcelable
import eu.depau.kotlet.android.parcelable.parcelableCreator
import org.poul.bits.android.lib.model.enum.BitsDataSource
import org.poul.bits.android.lib.model.enum.BitsStatus
import java.util.*

data class BitsData(
    val status: BitsStatus?,
    val modifiedBy: String?,
    val lastModified: Date?,
    val sensors: List<BitsSensorData>?,
    val message: BitsMessage?,
    val sensorsHistory: List<BitsSensorData>?,
    val source: BitsDataSource
) : KotletParcelable {
    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        status = parcel.readSerializable() as BitsStatus?,
        modifiedBy = parcel.readString(),
        lastModified = parcel.readSerializable() as Date?,
        sensors = parcel.readParcelableArray(BitsSensorData::class.java.classLoader)?.map { it as BitsSensorData },
        message = parcel.readParcelable<BitsMessage>(BitsMessage::class.java.classLoader),
        sensorsHistory = parcel.readParcelableArray(BitsSensorData::class.java.classLoader)?.map { it as BitsSensorData },
        source = parcel.readSerializable() as BitsDataSource
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(status)
        parcel.writeString(modifiedBy)
        parcel.writeSerializable(lastModified)
        parcel.writeParcelableArray(sensors?.toTypedArray(), flags)
        parcel.writeParcelable(message, flags)
        parcel.writeParcelableArray(sensorsHistory?.toTypedArray(), flags)
        parcel.writeSerializable(source)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsData)
    }
}