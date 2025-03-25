package com.android.account.book.ui.categoryList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.account.book.R
import com.android.account.book.data.model.Category
import com.android.account.book.databinding.FragmentCategoryListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryListFragment: Fragment(R.layout.fragment_category_list),
    CategoryListAdapter.OnItemClickListener {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CategoryListViewModel>()
    private lateinit var categoryAdapter: CategoryListAdapter
    lateinit var bottomSheetDialog: BottomSheetDialog
    private val args: CategoryListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryListAdapter(this)
        bottomSheetDialog = BottomSheetDialog(requireContext()!!)
        (activity as AppCompatActivity).supportActionBar?.title = "Category List"
        binding.apply {
            recyclerView.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            fabCreateCategory.setOnClickListener {
                showBottomSheetDialog()
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.getCategories(args.bookId).collectIndexed { _, value ->
                categoryAdapter.submitList(value)
            }
        }

        viewModel.responseMessage.observe(viewLifecycleOwner) {
            if (it.toString()!=""){
                Toast.makeText(this.activity, it.toString(), Toast.LENGTH_LONG).show()
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.add_book_bottomsheet_layout)
        bottomSheetDialog.findViewById<TextView>(R.id.tv_label)!!.text = "ADD NEW CATEGORY"
        val btAdd: Button = bottomSheetDialog.findViewById(R.id.bt_add)!!
        btAdd.text = "+ ADD NEW CATEGORY"
        btAdd.setOnClickListener {
            val title = bottomSheetDialog.findViewById<TextInputEditText>(R.id.et_title)?.text.toString()
            viewModel.addCategory(Category(name = title, book_id = args.bookId))
        }
        val btClose = bottomSheetDialog.findViewById<ImageView>(R.id.bt_close)!!
        btClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

    override fun onItemClick(category: Category) {
        setFragmentResult("selected_category", bundleOf("category" to category))
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


