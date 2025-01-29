package com.android.account.book.data.repository

import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Entry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {

    suspend fun addEntry(entry: Entry)

    fun getAllEntries(bookId: Int) : Flow<List<Entry>>

    fun getBookDetails(bookId: Int) : Book

    suspend fun updateEntry(entry: Entry)

    suspend fun deleteEntry(entry: Entry)
}