package com.example.books.ui.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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
        mutableIntStateOf(Math.ceil(booksCount.toDouble() / limit).toInt())
    }

    Row {
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

//        DropdownMenu(expanded = isPageMenuOpen, onDismissRequest = { setIsPageMenuOpen(false) }) {
//            (0..pagesOptionsSize).toList().forEach { item ->
//                DropdownMenuItem(
//                    text = { Text(text = item.toString()) },
//                    onClick = {
//                        setPage(item)
//                    }
//                )
//            }
//        }
    }
}