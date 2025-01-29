package com.android.account.book.data.repository

import com.android.account.book.data.model.Category
import com.android.account.book.db.dao.CategoryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepoImpl @Inject constructor(private val dao: CategoryDao): CategoryRepository {
    override suspend fun addCategory(category: Category) {
        return dao.addCategory(category)
    }

    override fun getAllCategories(bookId: Int): Flow<List<Category>> {
        return dao.getCategoriesOfBook(bookId)
    }
}