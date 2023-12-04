package com.example.books

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.books.data.remote.BooksService
import com.example.books.data.remote.dto.BookDto
import com.example.books.data.remote.dto.BooksWithCountDto
import com.example.books.data.remote.dto.CreateBookDto
import com.example.books.data.remote.dto.PaginationDto
import com.example.books.data.remote.dto.UpdateBookDto
import com.example.books.ui.theme.BooksTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val booksService = BooksService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val (showCreateBookDialog, setShowCreateBookDialog) = remember { mutableStateOf(false) }

            val (limit, setLimit) = remember { mutableIntStateOf(50) }
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
                            horizontalArrangement = Arrangement.End
                        ) {
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

const val MIN_STRING_LENGTH = 3
const val MIDDLE_STRING_LENGTH = 128
const val MIN_PUBLISH_YEAR = 1970

fun isTitleValid(title: String) = title.length in MIN_STRING_LENGTH..MIDDLE_STRING_LENGTH
fun isAuthorValid(author: String) = author.length in MIN_STRING_LENGTH..MIDDLE_STRING_LENGTH
fun isYearOfPublicationValid(yearOfPublication: String) =
    yearOfPublication.isDigitsOnly() && yearOfPublication.toInt() in MIN_PUBLISH_YEAR..LocalDate.now().year

@Composable
fun BookDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: suspend (String, String, Int) -> Unit,
    book: BookDto? = null
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var yearOfPublication by remember { mutableIntStateOf(book?.yearOfPublication ?: 0) }


    val isTitleError = remember(title) { !isTitleValid(title) }
    val isAuthorError = remember(author) { !isAuthorValid(author) }
    val isYearOfPublicationError =
        remember(yearOfPublication) { !isYearOfPublicationValid(yearOfPublication.toString()) }
    val isErrorForm by remember(
        isTitleError, isAuthorError, isYearOfPublicationError
    ) { mutableStateOf(isTitleError || isAuthorError || isYearOfPublicationError) }

    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text("Add New Book") },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isErrorForm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                onSave(title, author, yearOfPublication)
                            }
                        }
                    }, enabled = !isErrorForm
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlineTextFieldWithErrorMessage(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = { Text("Title") },
                        isError = isTitleError,
                        errorMessage = "Title must be between $MIN_STRING_LENGTH and $MIDDLE_STRING_LENGTH characters"
                    )
                    OutlineTextFieldWithErrorMessage(
                        value = author,
                        onValueChange = {
                            author = it
                        },
                        label = { Text("Author") },
                        isError = isAuthorError,
                        errorMessage = "Author must be between $MIN_STRING_LENGTH and $MIDDLE_STRING_LENGTH characters"
                    )
                    OutlineTextFieldWithErrorMessage(
                        value = yearOfPublication.toString(),
                        onValueChange = {
                            yearOfPublication = it.toInt()
                        },
                        label = { Text("Year of Publication") },
                        isError = isYearOfPublicationError,
                        errorMessage = "Year of publication must be a number between $MIN_PUBLISH_YEAR and the current year"
                    )
                }
            })
    }
}

@Composable
fun OutlineTextFieldWithErrorMessage(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable() (() -> Unit)?,
    isError: Boolean,
    errorMessage: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
    )
    if (isError) Text(
        text = errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall
    )
}
