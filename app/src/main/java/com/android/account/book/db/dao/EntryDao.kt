package com.android.account.book.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.account.book.data.model.Entry
import com.android.account.book.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM "+Constants.ENTRY_TABLE_NAME+ " WHERE book_id=:bookId")
    fun getAllEntriesOfBook(bookId: Int): Flow<List<Entry>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEntry(entry: Entry)

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

}