package com.android.account.book.data.repository

import com.android.account.book.data.model.Book
import com.android.account.book.db.dao.BookDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRepoImpl @Inject constructor(private val dao: BookDao) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> {
        return dao.getAllBooks()
    }

    override suspend fun addBook(book: Book) {
        dao.addBook(book)
    }

    override suspend fun updateBook(book: Book) {
        dao.updateBook(book)
    }

}