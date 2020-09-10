package org.poul.bits.android.lib.model

import android.os.Parcel
import eu.depau.kotlet.android.parcelable.KotletParcelable
import eu.depau.kotlet.android.parcelable.parcelableCreator
import java.util.*

data class BitsMessage(
    val user: String,
    val message: String,
    val lastModified: Date
) : KotletParcelable {
    constructor(parcel: Parcel) : this(
        user = parcel.readString()!!,
        message = parcel.readString()!!,
        lastModified = parcel.readSerializable() as Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeString(message)
        parcel.writeSerializable(lastModified)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::BitsMessage)
    }

    val empty: Boolean
        get() = message.trim().isBlank()
}