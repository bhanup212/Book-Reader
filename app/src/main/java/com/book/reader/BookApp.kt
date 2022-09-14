package com.book.reader

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.book.reader.di.BookComponent
import com.book.reader.di.BookLibModule
import com.book.reader.di.DaggerBookComponent

class BookApp: MultiDexApplication() {

    companion object{
        lateinit var bookComponent: BookComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger(){
        bookComponent = DaggerBookComponent.factory().create(BookLibModule(this))
    }
}