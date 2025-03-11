package com.android.account.book.ui.booklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.EventWrapper
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

    private val mutableResponseMessage = MutableLiveData<EventWrapper<String>>()
    val responseMessage: LiveData<EventWrapper<String>> get() = mutableResponseMessage

    fun setCurrentBook(book: Book?) {
        mutableBook.value = book
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.addBook(book)
            mutableResponseMessage.value = EventWrapper("Book added.")
        }
    }

    fun getAllBooks(): Flow<List<Book>> = repository.getAllBooks()

    fun updateBook(book: Book) {
        mutableBook.value = book
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun updateBookTitle(bookId: Int, title: String) {
        viewModelScope.launch {
            repository.updateBookTitle(bookId, title)
            mutableResponseMessage.value = EventWrapper("Book title updated.")
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

}