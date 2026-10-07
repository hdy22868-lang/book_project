package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MabhathDto(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String?,
    @SerializedName("sections") val sections: List<SectionDto>?
)