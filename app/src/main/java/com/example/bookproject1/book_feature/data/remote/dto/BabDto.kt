package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BabDto(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String?,
    @SerializedName("fasls") val fasls: List<FaslDto>?,
    @SerializedName("sections") val sections: List<SectionDto>?
)