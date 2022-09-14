package com.book.reader.data.model.request

import com.google.gson.annotations.SerializedName

data class EndBookSessionRequestModel(
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("time_spent")
    val timeSpent: Int,
    @SerializedName("end_time")
    val endTime: Long
)
