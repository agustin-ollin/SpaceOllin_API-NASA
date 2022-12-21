package com.ollin.rocketollin.models

import com.google.gson.annotations.SerializedName

data class NasaResponse(
    @SerializedName("copyright") var copyright: String,
    @SerializedName("date") var date: String,
    @SerializedName("explanation") var explanation: String,
    @SerializedName("hdurl") var hdurl: String,
    @SerializedName("media_type") var media_type: String,
    @SerializedName("service_version") var service_version: String,
    @SerializedName("title") var title: String,
    @SerializedName("url") var url: String
)
