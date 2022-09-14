package com.book.reader.di

import android.content.Context
import com.book.reader.data.IDispatchers
import com.book.reader.data.ISchedulers
import dagger.Module
import dagger.Provides

@Module
class BookLibModule(private val context: Context) {

    @Provides
    fun provideContext() = context

    @Provides
    fun provideISchedulers(): ISchedulers = Schedulers()

    @Provides
    fun provideCoroutineDispatchers(): IDispatchers = CoroutineDispatchers()
}