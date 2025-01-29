package com.android.account.book.ui.entrydetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.account.book.R
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Category
import com.android.account.book.data.model.Entry
import com.android.account.book.databinding.FragmentEntryDetailBinding
import com.android.account.book.ui.MainActivity
import com.android.account.book.ui.booklist.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class EntryDetailFragment:Fragment(R.layout.fragment_entry_detail), MenuProvider {

    private var _binding: FragmentEntryDetailBinding? = null
    private val binding get() = _binding!!
    private val args: EntryDetailFragmentArgs by navArgs()
    private var selectedCategory: Category? = null
    private val selectedBookViewModel by activityViewModels<BookListViewModel>()
    private val viewModel by viewModels<EntryDetailViewModel>()
    private var entryAmount : Int? = null
    private var menuDelete : MenuItem? = null

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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
                selectedCategory = Category(_id = args.entry!!.category_id, name = args.entry!!.category,
                    book_id = args.entry!!.book_id)
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
            Toast.makeText(this.activity, selectedCategory!!.name, Toast.LENGTH_LONG).show()
            binding.etCategory.setText(selectedCategory!!.name)
        }

        binding.btSave.setOnClickListener {
            if(type == 1 || type == -1) {
                val entry = Entry(entry_amount = binding.etAmount.text.toString().toInt(), entry_type = args.entryType,
                    description = binding.etRemark.text.toString(), book_id = args.bookId,
                    category_id = selectedCategory!!._id, category = selectedCategory!!.name)
                viewModel.addEntry(entry)
            } else {
                val entry = args.entry!!.copy(entry_amount = binding.etAmount.text.toString().toInt(),
                    description = binding.etRemark.text.toString(),
                    category_id = selectedCategory!!._id, category = selectedCategory!!.name)
                viewModel.updateEntry(entry)
            }
        }

        (activity as MainActivity).toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_delete) {
             Toast.makeText(activity, "toolbar menu", Toast.LENGTH_LONG).show()
            }
            false
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
                //findNavController().navigateUp()
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_list, menu)

        menuDelete = menu.findItem(R.id.action_delete)
        if (args.entry!= null) {
            menuDelete?.isVisible = true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                viewModel.deleteEntry(args.entry!!)
                true
            }
            else -> false
        }
    }

}