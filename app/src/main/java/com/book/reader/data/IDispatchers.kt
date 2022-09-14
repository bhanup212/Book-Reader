package com.book.reader.data

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    fun io(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
    fun computation(): CoroutineDispatcher
}