package com.android.account.book.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.account.book.util.Constants
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = Constants.ENTRY_TABLE_NAME)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val entry_amount: Int,
    val entry_type: Int,
    val description: String,
    val book_id: Int,
    val category_id: Int?,
    val category: String?,
    val updated_at: Long = System.currentTimeMillis()
) : Parcelable {
    val updatedDate: String
        get() = DateFormat.getDateInstance().format(updated_at)
}
