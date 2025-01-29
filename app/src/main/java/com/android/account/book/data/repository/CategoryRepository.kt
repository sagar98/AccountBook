package com.android.account.book.data.repository

import com.android.account.book.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun addCategory(category: Category)

    fun getAllCategories(bookId: Int): Flow<List<Category>>
}