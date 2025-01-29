package com.android.account.book.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize
import com.android.account.book.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.BOOKS_TABLE_NAME)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    var title: String,
    var book_amount: Int = 0, //1000
    var cash_in: Int = 0,    //  1200+100
    val cash_out: Int = 0,   //   200
    val created: Long = System.currentTimeMillis()
) : Parcelable
