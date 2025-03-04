package com.android.account.book.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookAdapter = BookListAdapter(this)
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

//        viewModel.responseMessage.observe(viewLifecycleOwner) {
//            if (it.toString() != "") {
//                Toast.makeText(this.activity, it.toString(), Toast.LENGTH_LONG).show()
//                bottomSheetDialog.dismiss()
//            }
//        }

        viewModel.responseMessage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                if(it!=null) {
                    Toast.makeText(this.activity, it.toString(), Toast.LENGTH_LONG).show()
                }
                bottomSheetDialog.dismiss()
            }
        })

    }

    fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.add_book_bottomsheet_layout)
        btAdd = bottomSheetDialog.findViewById(R.id.bt_add)!!
        btAdd.setOnClickListener {
            val title = bottomSheetDialog.findViewById<EditText>(R.id.et_title)?.text.toString()
            if (!title.isEmpty()) {
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

    override fun onOptionClick(book: Book) {
        TODO("Not yet implemented")
    }

}