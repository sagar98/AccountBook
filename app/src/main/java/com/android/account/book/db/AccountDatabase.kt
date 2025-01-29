package com.android.account.book.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Category
import com.android.account.book.data.model.Entry
import com.android.account.book.db.dao.BookDao
import com.android.account.book.db.dao.CategoryDao
import com.android.account.book.db.dao.EntryDao

@Database(entities = [Book::class, Category::class, Entry::class], version = 1, exportSchema = true)
abstract class AccountDatabase: RoomDatabase() {
    abstract fun BookDao(): BookDao
    abstract fun CategoryDao(): CategoryDao
    abstract fun EntryDao(): EntryDao
}