package com.book.reader.testrules

import com.book.reader.data.IDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

class CoroutineTestDispatcher: IDispatchers {
    @ExperimentalCoroutinesApi
    override fun io(): CoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    override fun main(): CoroutineDispatcher= TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    override fun computation(): CoroutineDispatcher = TestCoroutineDispatcher()
}