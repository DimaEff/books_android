package com.example.books.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.books.data.remote.BooksService
import com.example.books.data.remote.dto.BookDto

@Composable
fun BooksList(
    books: List<BookDto>, booksService: BooksService, handleFetchBooks: suspend () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.size) {
            BooksListItem(book = books[it], booksService = booksService, handleFetchBooks)
        }
    }
}