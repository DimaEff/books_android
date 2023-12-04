package com.example.books.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.books.data.remote.dto.BookDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

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

    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text("Add New Book") },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isErrorForm) {
                            CoroutineScope(Dispatchers.IO).launch {
                                onSave(title, author, yearOfPublication)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "The book has been added", Toast.LENGTH_SHORT).show()
                                }
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