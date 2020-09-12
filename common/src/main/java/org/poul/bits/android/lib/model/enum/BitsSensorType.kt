package org.poul.bits.android.lib.model.enum

enum class BitsSensorType(val sensorId: Int) {
    TEMPERATURE(0),
    HUMIDITY(1);

    companion object {
        private val map = BitsSensorType.values().associateBy(BitsSensorType::sensorId)

        fun fromInt(int: Int) = if (int in map) map[int] else null
    }
}