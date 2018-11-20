package eu.depau.poulbits.model

import eu.depau.poulbits.model.enum.BitsStatus
import java.util.*

data class BitsData(
    val status: BitsStatus,
    val modifiedBy: String,
    val lastModified: Date,
    val temperature: BitsTemperatureData,
    val message: BitsMessage,
    val temperatureHistory: List<BitsTemperatureData>
)