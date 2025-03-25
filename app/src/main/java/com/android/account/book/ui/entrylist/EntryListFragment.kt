package com.android.account.book.ui.entrylist

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.account.book.R
import com.android.account.book.data.model.Book
import com.android.account.book.data.model.Entry
import com.android.account.book.databinding.FragmentEntryListBinding
import com.android.account.book.ui.booklist.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntryListFragment : Fragment(R.layout.fragment_entry_list),
    EntryListAdapter.OnItemClickListener {

    private var _binding: FragmentEntryListBinding? = null
    private val binding get() = _binding!!
    private val selectedBookViewModel by activityViewModels<BookListViewModel>()
    private val viewModel by viewModels<EntryListViewModel>()
    private val args: EntryListFragmentArgs by navArgs()
    private lateinit var entryAdapter: EntryListAdapter
    private var deletedEntry: Entry? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = args.book?.title
        entryAdapter = EntryListAdapter(this)

        setUI()

        viewModel.getEntriesOfBook(args.book!!._id)

        if (selectedBookViewModel.currentBook.value != null) {
            selectedBookViewModel.currentBook.value.let {
                binding.apply {
                    rvEntry.adapter = entryAdapter
                    rvEntry.layoutManager = LinearLayoutManager(requireContext())
                    btnCashIn.setOnClickListener {
                        val action =
                            EntryListFragmentDirections.actionEntryListFragmentToEntryDetailFragment(
                                null,
                                1,
                                args.book!!._id
                            )
                        findNavController().navigate(action)
                    }
                    btnCashOut.setOnClickListener {
                        val action =
                            EntryListFragmentDirections.actionEntryListFragmentToEntryDetailFragment(
                                null,
                                -1,
                                args.book!!._id
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        viewModel.entriesList.observe(viewLifecycleOwner) {
            lifecycle.coroutineScope.launch {
                it.collectLatest {
                    entryAdapter.submitList(it)
                    if (it.toList().isEmpty()) {
                        binding.emptyView.isVisible = true
                    }
                }
            }
        }

        viewModel.responseMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled().let {
                if (it!=null && deletedEntry!=null) {
                    var book: Book? = null
                    if (deletedEntry!!.entry_type ==1) {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_in = selectedBookViewModel.currentBook.value!!.cash_in -
                                    deletedEntry!!.entry_amount)
                    } else {
                        book = selectedBookViewModel.currentBook.value!!.copy(
                            cash_out = selectedBookViewModel.currentBook.value!!.cash_out -
                                    deletedEntry!!.entry_amount)
                    }
                    book.book_amount = book.cash_in - book.cash_out
                    selectedBookViewModel.updateBook(book)
                    setUI()
                }
            }
        }
    }

    override fun onItemClick(entry: Entry) {
        val action =
            EntryListFragmentDirections.actionEntryListFragmentToEntryDetailFragment(entry)
        findNavController().navigate(action)
    }

    override fun onOptionsClick(view: View, entry: Entry) {
        val popup = PopupMenu(activity, view)
        // Inflating popup menu from popup_menu.xml file
        popup.inflate(R.menu.entry_menu_list)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_update -> {
                    val action =
                        EntryListFragmentDirections.actionEntryListFragmentToEntryDetailFragment(entry)
                    findNavController().navigate(action)
                }

                R.id.action_delete -> {
                    deletedEntry = entry
                    deleteEntry(entry)
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

    private fun setUI() {
        binding.tvCashIn.text = selectedBookViewModel.currentBook.value!!.cash_in.toString()
        binding.tvCashOut.text = selectedBookViewModel.currentBook.value!!.cash_out.toString()
        binding.tvBalance.text = selectedBookViewModel.currentBook.value!!.book_amount.toString()

        if (selectedBookViewModel.currentBook.value!!.book_amount > 0) {
            binding.tvBalance.setTextColor(resources.getColor(R.color.holo_green_dark))
        } else {
            binding.tvBalance.setTextColor(resources.getColor(R.color.holo_red_dark))
        }
    }

    private fun deleteEntry(entry: Entry) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Do you want to delete this entry ?")
        builder.setTitle("Delete Entry")

        builder.setPositiveButton("Yes") { dialog, which ->
            viewModel.deleteEntry(entry)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}