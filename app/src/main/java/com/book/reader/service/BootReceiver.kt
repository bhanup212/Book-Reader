package com.book.reader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.book.reader.BookApp
import com.book.reader.data.repository.SharedPreferenceRepository
import javax.inject.Inject

class BootReceiver: BroadcastReceiver() {
    @Inject
    lateinit var sharedPreferenceRepository: SharedPreferenceRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        BookApp.bookComponent.inject(this)
        if (sharedPreferenceRepository.getBookSessionStatus()) {
            val intent = Intent(context, BookService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context?.startForegroundService(intent)
            } else {
                context?.startService(intent)
            }
        }
    }
}