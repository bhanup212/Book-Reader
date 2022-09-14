package com.book.reader.data

import io.reactivex.rxjava3.core.Scheduler


interface ISchedulers {
    fun getIo(): Scheduler
    fun getComputation(): Scheduler
    fun getMainThread(): Scheduler
}