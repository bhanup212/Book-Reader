package com.book.reader.data.repository

import com.book.reader.data.ApiService
import com.book.reader.data.ISchedulers
import com.book.reader.data.model.request.EndBookSessionRequestModel
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class BookRepository @Inject constructor(private val apiService: ApiService, private val schedulers: ISchedulers){

    fun endBookSession(model: EndBookSessionRequestModel): Observable<JsonObject> {
       return apiService.endBookSession(model).subscribeOn(schedulers.getIo())
    }
}