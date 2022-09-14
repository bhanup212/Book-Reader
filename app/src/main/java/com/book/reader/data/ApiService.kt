package com.book.reader.data

import com.book.reader.data.model.request.EndBookSessionRequestModel
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/submit-session")
    fun endBookSession(
        @Body requestModel: EndBookSessionRequestModel
    ): Observable<JsonObject>
}