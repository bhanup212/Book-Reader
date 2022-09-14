package com.book.reader.data.repository

import android.content.Context
import androidx.core.content.edit
import com.book.reader.data.model.BookDetailsModel
import com.book.reader.di.BookReaderScope
import javax.inject.Inject
import javax.inject.Singleton

@BookReaderScope
class SharedPreferenceRepository @Inject constructor(context: Context) {
    companion object{
        private const val SHARED_PREF_NAME = "book_reader_prefs"
        private const val SESSION_STATUS = "session_status"
        private const val LOCATION_ID = "location_id"
        private const val LOCATION_DETAILS = "location_details"
        private const val PRICE_PER_MIN = "price_per_minute"
        private const val SESSION_START_TIME = "session_start_time"
    }

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun updateBookSession(isActive: Boolean){
        sharedPreferences.edit().putBoolean(SESSION_STATUS, isActive).apply()
    }

    fun getBookSessionStatus(): Boolean {
        return sharedPreferences.getBoolean(SESSION_STATUS, false)
    }

    fun setSessionStartTime(time: Long){
        sharedPreferences.edit().putLong(SESSION_START_TIME, time).apply()
    }

    fun getSessionStartTime(): Long {
        return sharedPreferences.getLong(SESSION_START_TIME, 0)
    }

    fun setSession(model: BookDetailsModel){
        sharedPreferences.edit {
            putString(LOCATION_ID, model.locationId)
            putString(LOCATION_DETAILS, model.locationDetails)
            putFloat(PRICE_PER_MIN, model.pricePerMin)
        }
    }

    fun getSessionDetails(): BookDetailsModel {
        val locationId = sharedPreferences.getString(LOCATION_ID,"") ?: ""
        val locationDetails = sharedPreferences.getString(LOCATION_DETAILS,"") ?: ""
        val pricePerMin = sharedPreferences.getFloat(PRICE_PER_MIN,0.0f)
        return BookDetailsModel(locationId, locationDetails, pricePerMin)
    }

    fun removeSession(){
        sharedPreferences.edit{
            remove(SESSION_START_TIME)
            remove(LOCATION_ID)
            remove(LOCATION_DETAILS)
            remove(SESSION_STATUS)
            remove(PRICE_PER_MIN)
        }
    }
}