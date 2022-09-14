package com.book.reader.viewmodel

import com.book.reader.data.ApiService
import com.book.reader.data.IDispatchers
import com.book.reader.data.ISchedulers
import com.book.reader.data.repository.BookRepository
import com.book.reader.testrules.BaseTestRule
import com.book.reader.testrules.CoroutineTestDispatcher
import com.book.reader.testrules.SchedulersTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class BookSessionViewModelTest: BaseTestRule() {
    @Mock
    private lateinit var apiService: ApiService
    private lateinit var schedulers: ISchedulers
    private lateinit var dispatcher: IDispatchers

    @Mock
    private lateinit var bookRepository: BookRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = CoroutineTestDispatcher()
        schedulers = SchedulersTest()
        Dispatchers.setMain(coroutineTestRule.testDispatcher)
        bookRepository = BookRepository(apiService, schedulers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}