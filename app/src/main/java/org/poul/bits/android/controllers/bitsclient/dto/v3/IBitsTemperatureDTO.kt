package org.poul.bits.android.controllers.bitsclient.dto.v3

import org.poul.bits.android.model.BitsSensorData

interface IBitsTemperatureDTO {
    fun toBitsTemperature(): BitsSensorData
}