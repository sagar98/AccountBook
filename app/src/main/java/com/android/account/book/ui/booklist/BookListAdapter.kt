package com.android.account.book.ui.booklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.account.book.R
import com.android.account.book.data.model.Book
import com.android.account.book.databinding.BookListItemBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class BookListAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Book, BookListAdapter.BookViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class BookViewHolder(private val binding: BookListItemBinding): RecyclerView.ViewHolder(
        binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION) {
                        val book = getItem(position)
                        listener.onItemClick(book)
                    }
                }
            }
        }

        fun bind(book: Book) {
            binding.txtTitle.text = book.title
            binding.txtAmount.text = book.book_amount.toString()
            if(book.book_amount>0) {
                binding.txtAmount.setTextColor(binding.parentCard.resources.getColor(R.color.holo_green_dark))
            } else {
                binding.txtAmount.setTextColor(binding.root.resources.getColor(R.color.holo_red_dark))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(book: Book)
        fun onOptionClick(book: Book)
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) =
            oldItem._id == newItem._id

        override fun areContentsTheSame(oldItem: Book, newItem: Book) =
            oldItem == newItem
    }
}