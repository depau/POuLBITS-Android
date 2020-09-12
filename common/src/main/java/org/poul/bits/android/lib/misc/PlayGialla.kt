package org.poul.bits.android.lib.misc

import android.content.Context
import android.media.MediaPlayer


fun Context.playGialla() {
    val giallaFd = assets.openFd("gialla.mp3")
    MediaPlayer().also {
        it.setDataSource(giallaFd.fileDescriptor, giallaFd.startOffset, giallaFd.length)
        it.prepare()
        it.start()
    }
}