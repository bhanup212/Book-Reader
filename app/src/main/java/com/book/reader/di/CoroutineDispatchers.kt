package com.book.reader.di

import com.book.reader.data.IDispatchers
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Singleton
class CoroutineDispatchers: IDispatchers {
    override fun io() = Dispatchers.IO

    override fun main() = Dispatchers.Main

    override fun computation() = Dispatchers.Default
}