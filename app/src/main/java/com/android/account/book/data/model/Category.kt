package com.android.account.book.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.account.book.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.CATEGORY_TABLE_NAME)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val name: String,
    val book_id: Int
) : Parcelable
