package com.example.books

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.books.ui.theme.BooksTheme
import fuel.Fuel
import fuel.FuelBuilder
import fuel.HttpResponse
import fuel.post
import fuel.request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooksTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BooksList(listOfBooks)
                }
            }
        }
    }
}

@Serializable
data class CreateBookDto(
    val title: String,
    val author: String,
    val yearOfPublication: Int
)

@Serializable
data class UpdateBookDto(
    val title: String? = null,
    val author: String? = null,
    val yearOfPublication: Int? = null
)

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val yearOfPublication: Int
)

val listOfBooks = listOf(
    Book(1, "Sample Book 1", "Sample Author 1", 2020),
    Book(1, "Sample Book 2", "Sample Author 2", 2019),
    Book(1, "Sample Book 3", "Sample Author 3", 2018),
    Book(1, "Sample Book 4", "Sample Author 4", 2017),
    Book(1, "Sample Book 5", "Sample Author 5", 2016),
    Book(1, "Sample Book 6", "Sample Author 6", 2015),
    Book(1, "Sample Book 7", "Sample Author 7", 2014),
    Book(1, "Sample Book 8", "Sample Author 8", 2013),
    Book(1, "Sample Book 9", "Sample Author 9", 2012),
    Book(1, "Sample Book 10", "Sample Author 10", 2011),
    Book(1, "Sample Book 1", "Sample Author 1", 2020),
    Book(1, "Sample Book 2", "Sample Author 2", 2019),
    Book(1, "Sample Book 3", "Sample Author 3", 2018),
    Book(1, "Sample Book 4", "Sample Author 4", 2017),
    Book(1, "Sample Book 5", "Sample Author 5", 2016),
    Book(1, "Sample Book 6", "Sample Author 6", 2015),
    Book(1, "Sample Book 7", "Sample Author 7", 2014),
    Book(1, "Sample Book 8", "Sample Author 8", 2013),
    Book(1, "Sample Book 9", "Sample Author 9", 2012),
)


@Composable
fun BooksList(books: List<Book>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.size) {
            BooksListItem(book = books[it])
        }
    }
}

@Composable
fun BooksListItem(book: Book) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    fun handleEditSave(title: String, author: String, yearOfPublication: Int) {
        // ...
        setShowDialog(false)
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
            IconButton(onClick = { setShowDialog(true) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        }
    }

    BookDialog(
        showDialog = showDialog,
        onDismiss = { setShowDialog(false) },
        onSave = ::handleEditSave,
        book
    )
}

const val MIN_STRING_LENGTH = 3
const val MIDDLE_STRING_LENGTH = 128
const val MIN_PUBLISH_YEAR = 1970

fun isTitleValid(title: String) = title.length in MIN_STRING_LENGTH..MIDDLE_STRING_LENGTH
fun isAuthorValid(author: String) = author.length in MIN_STRING_LENGTH..MIDDLE_STRING_LENGTH
fun isYearOfPublicationValid(yearOfPublication: String) =
    yearOfPublication.isDigitsOnly() && yearOfPublication.toInt() in MIN_PUBLISH_YEAR..LocalDate.now().year

class BooksApi {
    private val baseUrl = "http://localhost:3000/books"

    private suspend fun performRequest(url: String, method: String, headers: Map<String, String>, body: String) {
        val result = withContext(Dispatchers.IO) {
            Fuel.request(method, url)
                .header(headers)
                .body(body)
                .response()
        }

        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Success -> {
                    val (data, _) = result
                    // Handle the successful result data here
                }
                is Result.Failure -> {
                    val ex = result.getException()
                    // Handle the failure here
                }
            }
        }
    }

    suspend fun createBook(bookJson: String) {
        val url = "$baseUrl"
        val method = "POST"
        val headers = mapOf("Content-Type" to "application/json")
        performRequest(url, method, headers, bookJson)
    }

    suspend fun updateBook(bookId: Int, bookJson: String) {
        val url = "$baseUrl/$bookId"
        val method = "PUT"
        val headers = mapOf("Content-Type" to "application/json")
        performRequest(url, method, headers, bookJson)
    }
}

fun createBook(bookJson: String) {
    val url = "http://localhost:3000/books"
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch(Dispatchers.IO) {
        val result = Fuel.post(
            url = url,
            headers = mapOf("Content-Type" to "application/json"),
            body = bookJson
        )

        withContext(Dispatchers.Main) {
            when (result.statusCode) {
                in 200..299 -> {
                    val data = result.body
                    // Handle the successful result data here
                }

                else -> {

                }
            }
        }
    }
}

fun updateBook(bookId: Int, bookJson: String) {
    val url = "http://localhost:3000/books/$bookId"
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch(Dispatchers.IO) {
        val result = Fuel.post(
            url = url,
            headers = mapOf("Content-Type" to "application/json"),
            body = bookJson
        )

        withContext(Dispatchers.Main) {
            when (result.statusCode) {
                in 200..299 -> {
                    val data = result.body
                    // Handle the successful result data here
                }

                else -> {

                }
            }
        }
    }
}

@Composable
fun BookDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit,
    book: Book?
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var yearOfPublication by remember { mutableIntStateOf(book?.yearOfPublication ?: 0) }


    val isTitleError = remember(title) { !isTitleValid(title) }
    val isAuthorError = remember(author) { !isAuthorValid(author) }
    val isYearOfPublicationError =
        remember(yearOfPublication) { !isYearOfPublicationValid(yearOfPublication.toString()) }
    val isErrorForm by remember(
        isTitleError,
        isAuthorError,
        isYearOfPublicationError
    ) { mutableStateOf(isTitleError || isAuthorError || isYearOfPublicationError) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Book") },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isErrorForm) {
                            onSave(title, author, yearOfPublication)
                        }
                    },
                    enabled = !isErrorForm
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
            }
        )
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
        text = errorMessage,
        color = Color.Red,
        style = MaterialTheme.typography.bodySmall
    )
}
