package com.example.books.data.remote

import com.example.books.data.remote.dto.BookDto
import com.example.books.data.remote.dto.BooksWithCountDto
import com.example.books.data.remote.dto.CreateBookDto
import com.example.books.data.remote.dto.PaginationDto
import com.example.books.data.remote.dto.UpdateBookDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class BooksServiceImpl(
    private val httpClient: HttpClient,
) : BooksService {
    override suspend fun getAllBooks(pagination: PaginationDto): BooksWithCountDto? =
        handleRequest {
            httpClient.get(HttpRoutes.BOOKS) {
                url(HttpRoutes.BOOKS)
                parameter("limit", pagination.limit)
                parameter("page", pagination.page)
            }
        }?.body<BooksWithCountDto>()

    override suspend
    fun createBook(createBookDto: CreateBookDto): BookDto? =
        handleRequest {
            httpClient.post(HttpRoutes.BOOKS) {
                contentType(ContentType.Application.Json)
                setBody(createBookDto)
            }
        }?.body<BookDto>()

    override suspend
    fun updateBook(bookId: Int, updateBookDto: UpdateBookDto): BookDto? =
        handleRequest {
            httpClient.put("${HttpRoutes.BOOKS}/$bookId") {
                contentType(ContentType.Application.Json)
                setBody(updateBookDto)
            }
        }?.body<BookDto>()

    override suspend
    fun deleteBook(bookId: Int) {
        TODO("Not yet implemented")
    }

    private suspend fun handleRequest(requestFunc: suspend () -> HttpResponse): HttpResponse? =
        try {
            requestFunc()
        } catch (e: ResponseException) {
            println(e.response.status.description)
            null
        } catch (e: Exception) {
            println(e.message)
            null
        }
}