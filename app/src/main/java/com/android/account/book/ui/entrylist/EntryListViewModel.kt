package com.android.account.book.ui.entrylist

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Entry
import com.android.account.book.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class EntryListViewModel @Inject constructor(
    private val repository: EntryRepository
): ViewModel() {

//    private val mutableBook = MutableLiveData<Book?>()
//    val currentBook: LiveData<Book?> get() = mutableBook

    private val mutableEntriesList = MutableLiveData<Flow<List<Entry>>>()
    val entriesList: LiveData<Flow<List<Entry>>> get() = mutableEntriesList

//    fun setCurrentBook(book: Book?) {
//        mutableBook.value = book
//    }

    fun getEntriesOfBook(bookId: Int) {
        mutableEntriesList.value = repository.getAllEntries(bookId)
    }
}