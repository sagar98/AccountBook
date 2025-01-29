package com.android.account.book.data.repository

import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Entry
import com.android.account.book.db.dao.EntryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EntryRepoImpl @Inject constructor(private val dao: EntryDao): EntryRepository {
    override suspend fun addEntry(entry: Entry) {
        return dao.addEntry(entry)
    }

    override fun getBookDetails(bookId: Int): Book {
        TODO("Not yet implemented")
    }

    override suspend fun updateEntry(entry: Entry) {
        return dao.updateEntry(entry)
    }

    override suspend fun deleteEntry(entry: Entry) {
        return dao.deleteEntry(entry)
    }

    override fun getAllEntries(bookId: Int): Flow<List<Entry>> {
        return dao.getAllEntriesOfBook(bookId)
    }
}