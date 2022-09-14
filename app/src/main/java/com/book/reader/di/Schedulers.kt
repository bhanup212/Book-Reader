package com.book.reader.di

import com.book.reader.data.ISchedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Schedulers @Inject constructor(): ISchedulers{
    override fun getIo() = Schedulers.io()
    override fun getComputation() = Schedulers.computation()
    override fun getMainThread() = AndroidSchedulers.mainThread()
}