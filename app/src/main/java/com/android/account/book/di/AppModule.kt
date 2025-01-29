package com.android.account.book.di

import android.content.Context
import androidx.room.Room
import com.android.account.book.data.repository.BookRepoImpl
import com.android.account.book.data.repository.BookRepository
import com.android.account.book.data.repository.CategoryRepoImpl
import com.android.account.book.data.repository.CategoryRepository
import com.android.account.book.data.repository.EntryRepoImpl
import com.android.account.book.data.repository.EntryRepository
import com.android.account.book.db.AccountDatabase
import com.android.account.book.db.dao.BookDao
import com.android.account.book.db.dao.CategoryDao
import com.android.account.book.db.dao.EntryDao
import com.android.account.book.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun providesAccountDatabase(@ApplicationContext appContext: Context): AccountDatabase {
        return Room.databaseBuilder(
            appContext,
            AccountDatabase::class.java,
            Constants.DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(db: AccountDatabase): BookDao {
        return db.BookDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: AccountDatabase): CategoryDao {
        return db.CategoryDao()
    }

    @Provides
    @Singleton
    fun provideEntryDao(db: AccountDatabase): EntryDao {
        return db.EntryDao()
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDao: BookDao): BookRepository {
        return BookRepoImpl(bookDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepoImpl(categoryDao)
    }

    @Provides
    @Singleton
    fun provideEntryRepository(entryDao: EntryDao): EntryRepository {
        return EntryRepoImpl(entryDao)
    }
}