package com.android.account.book.ui.booklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.account.book.data.model.Book
import com.android.account.book.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    
    private val repository: BookRepository
) : ViewModel() {

    private val mutableBook = MutableLiveData<Book?>()
    val currentBook: LiveData<Book?> get() = mutableBook

    private val mutableResponseMessage = MutableLiveData("")
    val responseMessage: LiveData<String> get() = mutableResponseMessage

    fun setCurrentBook(book: Book?) {
        mutableBook.value = book
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.addBook(book)
            mutableResponseMessage.value = "Book added."
        }
    }

    fun getAllBooks(): Flow<List<Book>> = repository.getAllBooks()

    fun updateBook(book: Book) {
        mutableBook.value = book
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

}