package eu.depau.commons.android.kotlin.ktexts

import android.app.Activity

val Activity.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId)

        return 0
    }