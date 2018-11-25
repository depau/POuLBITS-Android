package org.poul.bits.android.controllers.widgetstorage

import org.poul.bits.android.model.BitsData

interface IWidgetStorageHelper {
    val bitsDataError: BitsData
    var loading: Boolean
    var bitsData: BitsData
    fun getWidgetHeightCells(appWidgetId: Int): Int
    fun setWidgetHeightCells(appWidgetId: Int, cells: Int)
}