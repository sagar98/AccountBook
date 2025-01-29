package com.android.account.book.ui.categoryList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.account.book.data.model.Category
import com.android.account.book.databinding.CategoryListItemBinding
import com.android.account.book.ui.booklist.BookListAdapter

class CategoryListAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListAdapter.CategoryViewHolder {
        val binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListAdapter.CategoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CategoryViewHolder(private val binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        listener.onItemClick(category)
                    }
                }
            }
        }

        fun bind(category: Category) {
            binding.txtTitle.text = category.name
        }
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem._id == newItem._id


        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem

    }
}