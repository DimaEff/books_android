package com.example.books.data.remote

import com.example.books.data.remote.dto.BookDto
import com.example.books.data.remote.dto.BooksWithCountDto
import com.example.books.data.remote.dto.CreateBookDto
import com.example.books.data.remote.dto.PaginationDto
import com.example.books.data.remote.dto.UpdateBookDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class BooksServiceImpl(
    private val httpClient: HttpClient,
) : BooksService {
    override suspend fun getAllBooks(pagination: PaginationDto): BooksWithCountDto? {
        return try {
            httpClient.get(HttpRoutes.BOOKS) {
                url(HttpRoutes.BOOKS)
                parameter("limit", pagination.limit)
                parameter("page", pagination.page)
            }.body<BooksWithCountDto>()
        } catch(e: RedirectResponseException) {
            println("Error: ${e.response.status.description}")
            null
        } catch(e: ClientRequestException) {
            println("Error: ${e.response.status.description}")
            null
        } catch(e: ServerResponseException) {
            println("Error: ${e.response.status.description}")
            null
        } catch(e: Exception) {
            println("Error: $e")
            null
        }
    }

    override suspend fun createBook(createBookDto: CreateBookDto): BookDto? {
        TODO("Not yet implemented")
    }

    override suspend fun updateBook(bookId: Int, updateBookDto: UpdateBookDto): BookDto? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBook(bookId: Int) {
        TODO("Not yet implemented")
    }
}