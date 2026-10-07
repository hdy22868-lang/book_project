package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FaslDto(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String?,
    @SerializedName("mabhaths") val mabhaths: List<MabhathDto>?,
    @SerializedName("sections") val sections: List<SectionDto>?
)