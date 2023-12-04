package com.example.books

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.books.data.remote.BooksService
import com.example.books.data.remote.dto.BooksWithCountDto
import com.example.books.data.remote.dto.CreateBookDto
import com.example.books.data.remote.dto.PaginationDto
import com.example.books.ui.theme.BookDialog
import com.example.books.ui.theme.BooksList
import com.example.books.ui.theme.BooksTheme
import com.example.books.ui.theme.PaginationControl

class MainActivity : ComponentActivity() {
    private val booksService = BooksService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val (showCreateBookDialog, setShowCreateBookDialog) = remember { mutableStateOf(false) }

            val (limit, setLimit) = remember { mutableIntStateOf(5) }

            val (page, setPage) = remember { mutableIntStateOf(0) }

            val (books, setBooks) = remember { mutableStateOf<BooksWithCountDto?>(null) }
            suspend fun handleFetchBooks() =
                setBooks(booksService.getAllBooks(PaginationDto(limit, page)))
            LaunchedEffect(limit, page, block = { handleFetchBooks() })

            BooksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (books != null) PaginationControl(
                                limit,
                                setLimit,
                                books.count,
                                page,
                                setPage
                            )
                            IconButton(onClick = { setShowCreateBookDialog(true) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                        }
                        if (books != null) {
                            BooksList(books.rows, booksService, ::handleFetchBooks)
                        } else {
                            Text(modifier = Modifier.fillMaxWidth(), text = "There are no books")
                        }
                    }

                    BookDialog(showDialog = showCreateBookDialog,
                        onDismiss = { setShowCreateBookDialog(false) },
                        onSave = { title, author, yearOfPublication ->
                            booksService.createBook(
                                CreateBookDto(title, author, yearOfPublication)
                            )
                            handleFetchBooks()
                            setShowCreateBookDialog(false)
                        })
                }
            }
        }
    }
}
