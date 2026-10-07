package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName
data class BookTreeDto(
    @SerializedName("introductions") val introductions: List<IntroDto>?,
    @SerializedName("babs") val babs: List<BabDto>?,
    @SerializedName("general") val general: List<String>?
)