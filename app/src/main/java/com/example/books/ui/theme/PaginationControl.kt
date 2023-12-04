package com.example.books.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.books.utils.toUserNumber
import kotlin.math.floor

@Composable
fun PaginationControl(
    limit: Int,
    setLimit: (limit: Int) -> Unit,
    booksCount: Int,
    page: Int,
    setPage: (page: Int) -> Unit
) {
    val (isLimitMenuOpen, setIsLimitMenuOpen) = remember {
        mutableStateOf(false)
    }
    val limitOptions = listOf(5, 10, 20, 50, 60)


    val (isPageMenuOpen, setIsPageMenuOpen) = remember {
        mutableStateOf(false)
    }
    val pagesOptionsSize by remember(booksCount, limit) {
        mutableIntStateOf(floor(booksCount.toDouble() / limit).toInt())
    }

    Row(modifier = Modifier.height(56.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Column {
            Button(onClick = { setIsLimitMenuOpen(true) }) {
                Text(text = "Limit: $limit")
            }
            DropdownMenu(expanded = isLimitMenuOpen, onDismissRequest = { setIsLimitMenuOpen(false) }) {
                limitOptions.map { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.toString()) },
                        onClick = {
                            setLimit(item)
                        }
                    )
                }
            }
        }

        Column {
            Button(onClick = { setIsPageMenuOpen(true) }) {
                Text(text = "Page: ${toUserNumber(page)}")
            }
            DropdownMenu(expanded = isPageMenuOpen, onDismissRequest = { setIsPageMenuOpen(false) }) {
                (0..pagesOptionsSize).toList().forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = toUserNumber(item).toString()) },
                        onClick = {
                            setPage(item)
                        }
                    )
                }
            }
        }
    }
}