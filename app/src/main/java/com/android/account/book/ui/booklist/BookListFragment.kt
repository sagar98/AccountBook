package com.android.account.book.ui.booklist

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.account.book.R
import com.android.account.book.data.model.Book
import com.android.account.book.databinding.FragmentBookListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment(R.layout.fragment_book_list),
    BookListAdapter.OnItemClickListener {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var btAdd: Button
    lateinit var btClose: ImageView
    private val viewModel by activityViewModels<BookListViewModel>()
    private lateinit var bookAdapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookAdapter = BookListAdapter(this, context)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        binding.apply {
            recyclerView.apply {
                adapter = bookAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            fabCreateBook.setOnClickListener {
                showBottomSheetDialog()
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.getAllBooks().collectIndexed { _, value ->
                bookAdapter.submitList(value)
                if (value.isEmpty()) {
                    binding.emptyView.isVisible = true
                }
            }
        }

        viewModel.responseMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled().let {
                if (it != null) {
                    //Toast.makeText(this.activity, it.toString(), Toast.LENGTH_LONG).show()
                    if (binding.emptyView.isVisible) {
                        binding.emptyView.isVisible = false
                    }
                }
                bottomSheetDialog.dismiss()
            }
        }
    }

    fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.add_book_bottomsheet_layout)
        btAdd = bottomSheetDialog.findViewById(R.id.bt_add)!!
        btAdd.setOnClickListener {
            val title = bottomSheetDialog.findViewById<EditText>(R.id.et_title)?.text.toString()
            if (title.isNotEmpty()) {
                viewModel.addBook(Book(title = title))
            } else {
                Toast.makeText(activity, "Please enter title for book.", Toast.LENGTH_LONG).show()
            }
        }
        btClose = bottomSheetDialog.findViewById(R.id.bt_close)!!
        btClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(book: Book) {
        val action = BookListFragmentDirections.actionBookListFragmentToEntryListFragment(book)
        viewModel.setCurrentBook(book)
        findNavController().navigate(action)
    }

    override fun onOptionClick(view: View, book: Book) {
        val popup = PopupMenu(activity, view)
        // Inflating popup menu from popup_menu.xml file
        popup.inflate(R.menu.book_menu_list)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    deleteBook(book)
                }

                R.id.action_rename -> {
                    renameBook(book)
                }
            }
            true
        }
        try {
            val iconPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            iconPopup.isAccessible = true
            val mPopup = iconPopup.get(popup)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Error", "Error in showing menu icons")
        } finally {
            popup.show()
        }
    }

    private fun renameBook(book: Book) {
        bottomSheetDialog.setContentView(R.layout.add_book_bottomsheet_layout)
        var tvLabel = bottomSheetDialog.findViewById<TextView>(R.id.tv_label)!!
        tvLabel.text = "Update Book Title"
        var btAdd = bottomSheetDialog.findViewById<Button>(R.id.bt_add)!!
        var etTitle = bottomSheetDialog.findViewById<EditText>(R.id.et_title)!!
        etTitle.setText(book.title)
        btAdd.text = "Update Title"
        btAdd.setOnClickListener {
            var title = etTitle.text.toString()
            if (title.isNotEmpty()) {
                viewModel.updateBookTitle(book._id, title)
            } else {
                Toast.makeText(activity, "Please enter title for book.", Toast.LENGTH_LONG).show()
            }
        }
        var btClose = bottomSheetDialog.findViewById<ImageView>(R.id.bt_close)!!
        btClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun deleteBook(book: Book) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("It will delete all entries and categories of this book. Do you still want to delete this book ?")
        builder.setTitle("Delete Book")

        builder.setPositiveButton("Yes") { dialog, which ->
            viewModel.deleteBook(book)

        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}