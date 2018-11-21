package org.poul.bits.android.misc

import android.content.Context
import android.media.MediaPlayer


fun Context.playGialla() {
    val giallaFd = assets.openFd("gialla.ogg")
    MediaPlayer().also {
        it.setDataSource(giallaFd.fileDescriptor, giallaFd.startOffset, giallaFd.length);
        it.prepare()
        it.start()
    }
}