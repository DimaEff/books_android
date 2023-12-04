package com.example.books.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable()
data class UpdateBookDto(
    val title: String? = null,
    val author: String? = null,
    val yearOfPublication: Int? = null,
)
