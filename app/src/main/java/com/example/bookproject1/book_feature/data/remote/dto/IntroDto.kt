package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName
data class IntroDto(
    @SerializedName("name") val name: String,
    @SerializedName("sections") val sections: List<SectionDto>?
)