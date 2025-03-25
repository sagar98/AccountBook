package com.android.account.book.ui.entrydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.account.book.R
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Category
import com.android.account.book.data.model.Entry
import com.android.account.book.databinding.FragmentEntryDetailBinding
import com.android.account.book.ui.booklist.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryDetailFragment:Fragment(R.layout.fragment_entry_detail) {

    private var _binding: FragmentEntryDetailBinding? = null
    private val binding get() = _binding!!
    private val args: EntryDetailFragmentArgs by navArgs()
    private var selectedCategory: Category? = null
    private val selectedBookViewModel by activityViewModels<BookListViewModel>()
    private val viewModel by viewModels<EntryDetailViewModel>()
    private var entryAmount : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEntryDetailBinding.bind(view)

        val type = args.entryType
        if(type == 1) {
            (activity as AppCompatActivity).supportActionBar?.title = "Add Cash In Entry"
        } else if (type == -1) {
            (activity as AppCompatActivity).supportActionBar?.title = "Add Cash Out Entry"
        } else {
            if(args.entry!= null) {
                entryAmount = args.entry?.entry_amount
                if(args.entry!!.entry_type == 1) {
                    (activity as AppCompatActivity).supportActionBar?.title = "Update Cash In Entry"
                } else {
                    (activity as AppCompatActivity).supportActionBar?.title = "Update Cash Out Entry"
                }
                binding.etAmount.setText(args.entry!!.entry_amount.toString())
                binding.etCategory.setText(args.entry!!.category)
                binding.etRemark.setText(args.entry!!.description)
                if(args.entry!!.category_id!= null) {
                    selectedCategory = Category(_id = args.entry!!.category_id!!,
                        name = args.entry!!.category!!, book_id = args.entry!!.book_id)
                }
                binding.btSave.text = "UPDATE"

            }
        }

        binding.etCategory.setOnClickListener {
            val bookId = args.bookId
            val action = EntryDetailFragmentDirections.actionEntryDetailFragmentToCategoryListFragment(bookId)
            findNavController().navigate(action)
        }

        setFragmentResultListener("selected_category") { _, bundle ->
            selectedCategory = bundle.getParcelable("category")
            //Toast.makeText(this.activity, selectedCategory!!.name, Toast.LENGTH_LONG).show()
            binding.etCategory.setText(selectedCategory!!.name)
        }

        binding.btSave.setOnClickListener {
            if(binding.etAmount.text.toString().isNotEmpty()) {
                if(type == 1 || type == -1) {
                    if(selectedCategory!= null) {
                        val entry = Entry(
                            entry_amount = binding.etAmount.text.toString().toInt(), entry_type = args.entryType,
                            description = binding.etRemark.text.toString(), book_id = args.bookId,
                            category_id = selectedCategory!!._id, category = selectedCategory!!.name)
                        viewModel.addEntry(entry)
                    } else {
                        val entry = Entry(
                            entry_amount = binding.etAmount.text.toString().toInt(), entry_type = args.entryType,
                            description = binding.etRemark.text.toString(), book_id = args.bookId,
                            category_id = null, category = null)
                        viewModel.addEntry(entry)
                    }

                } else {
                    if(selectedCategory!= null) {
                        val entry = args.entry!!.copy(entry_amount = binding.etAmount.text.toString().toInt(),
                            description = binding.etRemark.text.toString(),
                            category_id = selectedCategory!!._id, category = selectedCategory!!.name)
                        viewModel.updateEntry(entry)
                    } else {
                        val entry = args.entry!!.copy(entry_amount = binding.etAmount.text.toString().toInt(),
                            description = binding.etRemark.text.toString())
                        viewModel.updateEntry(entry)
                    }

                }
            } else {
                Toast.makeText(this.activity, "Please enter amount.", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.actionType.observe(viewLifecycleOwner) {
            if(it == 1 || it == 2) {
                var book: Book? = null
                if (it == 1) {
                    if (args.entryType == 1) {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_in =
                            selectedBookViewModel.currentBook.value!!.cash_in + binding.etAmount.text.toString()
                                .toInt()
                        )
                    } else {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_out =
                            selectedBookViewModel.currentBook.value!!.cash_out + binding.etAmount.text.toString()
                                .toInt()
                        )
                    }
                } else if (it == 2) {
                    if (args.entry!!.entry_type == 1) {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_in =
                            (selectedBookViewModel.currentBook.value!!.cash_in - entryAmount!!) + binding.etAmount.text.toString()
                                .toInt()
                        )
                    } else {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_out =
                            (selectedBookViewModel.currentBook.value!!.cash_out - entryAmount!!) + binding.etAmount.text.toString()
                                .toInt()
                        )
                    }
                }
                book!!.book_amount = book!!.cash_in - book!!.cash_out

                selectedBookViewModel.updateBook(book)
                findNavController().navigateUp()
            }
        }
    }

}