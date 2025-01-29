package com.android.account.book.data.repository

import com.android.account.book.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun getAllBooks(): Flow<List<Book>>

    suspend fun addBook(book: Book)

    suspend fun updateBook(book: Book)
}