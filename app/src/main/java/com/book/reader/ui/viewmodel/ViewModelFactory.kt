package com.book.reader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.book.reader.data.IDispatchers
import com.book.reader.data.ISchedulers
import com.book.reader.data.repository.BookRepository
import com.book.reader.data.repository.SharedPreferenceRepository
import com.book.reader.di.BookReaderScope
import com.book.reader.di.CoroutineDispatchers
import javax.inject.Inject

@BookReaderScope
class ViewModelFactory @Inject constructor(
    private val bookRepository: BookRepository,
    private val schedulers: ISchedulers,
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val coroutineDispatchers: IDispatchers
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookSessionViewModel::class.java)) {
            return BookSessionViewModel(bookRepository, schedulers, sharedPreferenceRepository, coroutineDispatchers) as T
        }
        throw IllegalArgumentException("$modelClass ViewModel Not Found")
    }
}