package com.android.account.book.ui.entrylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.account.book.R
import com.android.account.book.data.model.Entry
import com.android.account.book.databinding.FragmentEntryListBinding
import com.android.account.book.ui.MainActivity
import com.android.account.book.ui.booklist.BookListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
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
                }
            }
        }
    }

    override fun onItemClick(entry: Entry) {
        val action =
            EntryListFragmentDirections.actionEntryListFragmentToEntryDetailFragment(entry)
        findNavController().navigate(action)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}