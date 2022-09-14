package com.book.reader.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.book.reader.BookApp
import com.book.reader.R
import com.book.reader.data.repository.SharedPreferenceRepository
import com.book.reader.service.BookService
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var bookService: BookService? = null

    private val _isBookServiceConnected = MutableLiveData<Boolean>(false)
    val isBookServiceConnected: LiveData<Boolean> = _isBookServiceConnected

    @Inject
    lateinit var sharedPreferenceRepository: SharedPreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BookApp.bookComponent.inject(this)
        val fragment = supportFragmentManager.findFragmentByTag(BookSessionFragment.TAG)
        if (fragment == null){
            openHomeFragment()
        }
        if (sharedPreferenceRepository.getBookSessionStatus()){
            bindService()
        }
    }

    private fun openHomeFragment(){
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,BookSessionFragment.newInstance(), BookSessionFragment.TAG)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getBookService(): BookService? = bookService

    private val bookServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bookService = (service as BookService.BookBinder).service
            _isBookServiceConnected.value = true
            bookService?.removeNotification()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _isBookServiceConnected.value = false
        }
    }

    fun bindService(){
        Intent(this, BookService::class.java).also { intent ->
            intent.putExtra(BookService.IS_SHOW_NOTIFICATION, false)
            bindService(intent, bookServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun undBindService(){
        if (isBookServiceConnected.value == true) {
            Intent(this, BookService::class.java).also { intent ->
                unbindService(bookServiceConnection)
                _isBookServiceConnected.value = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
       if (sharedPreferenceRepository.getBookSessionStatus()) startForegroundService()
        bookService = null
    }

    private fun startForegroundService(){
        val intent = Intent(this, BookService::class.java)
        intent.putExtra(BookService.IS_SHOW_NOTIFICATION, true)
        startService(intent)
    }
}