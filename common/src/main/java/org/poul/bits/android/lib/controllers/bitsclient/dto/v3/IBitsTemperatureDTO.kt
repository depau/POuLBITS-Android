package org.poul.bits.android.lib.controllers.bitsclient.dto.v3

import org.poul.bits.android.lib.model.BitsSensorData

interface IBitsTemperatureDTO {
    fun toBitsTemperature(): BitsSensorData
}