package com.book.reader.repository

import com.book.reader.data.ApiService
import com.book.reader.data.ISchedulers
import com.book.reader.data.model.request.EndBookSessionRequestModel
import com.book.reader.data.repository.BookRepository
import com.book.reader.testrules.SchedulersTest
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class BookRepositoryTest {

    @Mock
    private lateinit var apiService: ApiService
    private lateinit var schedulers: ISchedulers

    private lateinit var bookRepository:BookRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        schedulers = SchedulersTest()
        bookRepository = BookRepository(apiService, schedulers)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test_end_session_should_return_success(){
        val model = EndBookSessionRequestModel("butter-knife",2,12345678)
        val response = JsonObject().apply {
            addProperty("success", true)
        }
        Mockito.`when`(apiService.endBookSession(model)).thenReturn(Observable.just(response))
        val observableRes = bookRepository.endBookSession(model).test()
        Assert.assertNotNull(observableRes)
    }

}