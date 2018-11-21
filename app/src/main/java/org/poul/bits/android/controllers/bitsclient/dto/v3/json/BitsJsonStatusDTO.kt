package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import java.util.*

class BitsJsonStatusDTO(
    var value: String?,
    var modifiedBy: String?,
    var timestamp: Date?
) {
    constructor() : this(null, null, null)
}