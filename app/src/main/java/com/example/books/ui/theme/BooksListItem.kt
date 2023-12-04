package com.example.books.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.books.data.remote.BooksService
import com.example.books.data.remote.dto.BookDto
import com.example.books.data.remote.dto.UpdateBookDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun BooksListItem(book: BookDto, booksService: BooksService, handleFetchBooks: suspend () -> Unit) {
    val (showEditBookDialog, setShowEditBookDialog) = remember { mutableStateOf(false) }
    val (showDeleteBookDialog, setShowDeleteBookDialog) = remember { mutableStateOf(false) }

    suspend fun handleEditSave(title: String, author: String, yearOfPublication: Int) {
        booksService.updateBook(book.id, UpdateBookDto(title, author, yearOfPublication))
        handleFetchBooks()
        setShowEditBookDialog(false)
    }

    suspend fun handleDeleteBook(bookId: Int) {
        booksService.deleteBook(bookId)
        handleFetchBooks()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(text = book.author, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = book.yearOfPublication.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                IconButton(onClick = { setShowEditBookDialog(true) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = {
                    setShowDeleteBookDialog(true)
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    BookDialog(
        showDialog = showEditBookDialog,
        onDismiss = { setShowEditBookDialog(false) },
        onSave = ::handleEditSave,
        book
    )

    if (showDeleteBookDialog) AlertDialog(
        title = { Text(text = "Do you want to delete a book?") },
        onDismissRequest = { setShowDeleteBookDialog(false) },
        dismissButton = {
            TextButton(onClick = {
                setShowDeleteBookDialog(false)
            }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    coroutineScope {
                        handleDeleteBook(book.id)
                    }
                }
                setShowDeleteBookDialog(false)
            }) {
                Text("Delete")
            }
        },
    )
}