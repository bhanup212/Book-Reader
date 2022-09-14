package com.book.reader.utils

import java.util.concurrent.TimeUnit

object TimeUtils {
    fun Long.toMinutes(): Long {
        return TimeUnit.MILLISECONDS
            .toMinutes(this)
    }

    fun Long.convertSecondsToHMmSs(): String {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)))
    }
}