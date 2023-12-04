package com.example.books.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BooksWithCountDto(
    val count: Int,
    val rows: List<BookDto>,
)
