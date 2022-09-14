package com.book.reader.di

import com.book.reader.service.BootReceiver
import com.book.reader.ui.BookSessionFragment
import com.book.reader.ui.MainActivity
import com.book.reader.ui.SessionConfirmFragment
import dagger.Component

@BookReaderScope
@Component(modules = [BookLibModule::class, BookNetworkModule::class])
interface BookComponent {
    fun inject(bookSessionFragment: BookSessionFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(sessionConfirmFragment: SessionConfirmFragment)
    fun inject(bootReceiver: BootReceiver)

    @Component.Factory
    interface Factory {
        fun create(module: BookLibModule): BookComponent
    }
}