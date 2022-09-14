package com.book.reader.testrules

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

open class BaseTestRule {

    @get: Rule
    var coroutineTestRule = CoroutineTestRule()

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
}