package com.example.books.data.remote

import com.example.books.data.remote.dto.BookDto
import com.example.books.data.remote.dto.BooksWithCountDto
import com.example.books.data.remote.dto.CreateBookDto
import com.example.books.data.remote.dto.PaginationDto
import com.example.books.data.remote.dto.UpdateBookDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

interface BooksService {
    suspend fun getAllBooks(pagination: PaginationDto): BooksWithCountDto?
    suspend fun createBook(createBookDto: CreateBookDto): BookDto?
    suspend fun updateBook(bookId: Int, updateBookDto: UpdateBookDto): BookDto?
    suspend fun deleteBook(bookId: Int): Unit

    companion object {
        fun create() : BooksService {
            return BooksServiceImpl(
                httpClient = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(ContentNegotiation) {
                        json()
                    }
                }
            )
        }
    }
}