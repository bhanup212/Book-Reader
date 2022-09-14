package com.book.reader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.book.reader.R
import com.book.reader.data.repository.SharedPreferenceRepository
import com.book.reader.utils.TimeUtils.convertSecondsToHMmSs
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class BookService: LifecycleService() {

    companion object {
        private const val NOTIFICATION_ID = 128
        private const val NOTIFICATION_CHANNEL_ID = "book_reader_channel_id"
        private const val NOTIFICATION_CHANNEL_NAME = "book_reader"
        const val IS_SHOW_NOTIFICATION = "is_show_notification"
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var currentNotification: NotificationCompat.Builder

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private var startTime = System.currentTimeMillis()
    private var isShowNotification = true

    private lateinit var sharedPreferenceRepository: SharedPreferenceRepository
    private var binder = BookBinder()

    inner class BookBinder: Binder() {
        val service: BookService
            get() = this@BookService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferenceRepository = SharedPreferenceRepository(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        currentNotification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Book Session Active")
            .setContentText("Your session is running, to stop scan the qr code")
            .setSmallIcon(R.drawable.ic_baseline_menu_book)
        startTime = sharedPreferenceRepository.getSessionStartTime()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startTimer()
        observeTimerState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isShowNotification = intent?.getBooleanExtra(IS_SHOW_NOTIFICATION, true) ?: true
        startForegroundService()
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun observeTimerState(){
        timerText.observe(this, Observer { txt ->
            if (isShowNotification) {
                currentNotification.setContentTitle("Book Session Active - ( $txt )")
                notificationManager.notify(NOTIFICATION_ID, currentNotification.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun startForegroundService(){
        if (isShowNotification) {
            startForeground(NOTIFICATION_ID, currentNotification.build())
        } else {
            removeNotification()
        }
    }

    fun removeNotification(){
        isShowNotification = false
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(true)
    }

    private fun startTimer(){
        coroutineScope.launch {
            while (true && isActive){
                _timerText.postValue((System.currentTimeMillis()-startTime).convertSecondsToHMmSs())
                delay(1000)
            }
        }

    }

    private fun stopTimer(){
        if (coroutineScope.isActive) coroutineScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        removeNotification()
    }
}