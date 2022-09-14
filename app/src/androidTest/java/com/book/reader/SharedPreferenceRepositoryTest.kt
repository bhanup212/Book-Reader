package com.book.reader

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.book.reader.data.repository.SharedPreferenceRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferenceRepositoryTest {

    private lateinit var sharedPreferenceRepository: SharedPreferenceRepository
    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferenceRepository = SharedPreferenceRepository(appContext)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test_start_session_should_return_true(){
        sharedPreferenceRepository.updateBookSession(true)
        Assert.assertEquals(true, sharedPreferenceRepository.getBookSessionStatus())
    }

    @Test
    fun test_session_start_should_return_12345(){
        sharedPreferenceRepository.setSessionStartTime(12345)
        Assert.assertEquals(12345, sharedPreferenceRepository.getSessionStartTime())
    }
}