package com.android.account.book.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.android.account.book.data.model.Book
import com.android.account.book.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM " + Constants.BOOKS_TABLE_NAME+ " ORDER BY created DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Insert
    suspend fun addBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Query("UPDATE books_table SET title=:title WHERE _id=:bookId")
    suspend fun updateBookTitle(bookId: Int, title: String)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM category_table WHERE book_id=:bookId")
    suspend fun deleteBookCategories(bookId: Int)

    @Query("DELETE FROM entry_table WHERE book_id=:bookId")
    suspend fun deleteBookEntries(bookId: Int)

    @Transaction
    suspend fun deleteBookData(book: Book) {
        deleteBook(book)
        deleteBookCategories(book._id)
        deleteBookEntries(book._id)
    }

}