package com.example.prioritybasedpopup.ui.theme.popup

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prioritybasedpopup.databinding.FragmentItemListDialogListDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class ItemListDialogFragment(
    private val next: () -> Unit,
    private val priority: Int
) : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        binding.textview.text = "priority: $priority"
        return binding.root

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("ItemListDialogFragment", "dialog onDismiss")
        next()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(next: () -> Unit, priority: Int): ItemListDialogFragment = ItemListDialogFragment(next, priority)
    }
}