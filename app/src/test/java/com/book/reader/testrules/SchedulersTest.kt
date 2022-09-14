package com.book.reader.testrules

import com.book.reader.data.ISchedulers
import io.reactivex.rxjava3.schedulers.TestScheduler

class SchedulersTest: ISchedulers {
    override fun getIo()= TestScheduler()

    override fun getComputation()= TestScheduler()

    override fun getMainThread()= TestScheduler()
}