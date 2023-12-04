package com.example.books.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val id: Int,
    val title: String,
    val author: String,
    val yearOfPublication: Int,
)
