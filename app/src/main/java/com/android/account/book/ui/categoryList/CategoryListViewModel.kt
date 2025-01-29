package com.android.account.book.ui.categoryList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.account.book.data.model.Category
import com.android.account.book.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val repository: CategoryRepository
): ViewModel() {

    private val mutableResponseMessage = MutableLiveData("")
    val responseMessage: LiveData<String> get() = mutableResponseMessage

    fun addCategory(category: Category) {
        viewModelScope.launch {
            repository.addCategory(category)
            mutableResponseMessage.value = "Category added."
        }
    }

    fun getCategories(bookId: Int): Flow<List<Category>> = repository.getAllCategories(bookId)
}