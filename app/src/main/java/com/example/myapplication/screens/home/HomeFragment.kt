package com.example.myapplication.screens.home


import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.CircleColor
import com.example.myapplication.data.CircleColorList
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.db.Note
import com.example.myapplication.getThemeColor
import com.example.myapplication.poop
import com.example.myapplication.screens.home.recycler.NoteAdapter
import com.example.myapplication.screens.home.recyclercolor.ColorAdapter
import com.example.myapplication.viemodel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), NoteAdapter.NoteItemListener, ColorAdapter.ColorItemListener {
    private lateinit var binding: FragmentHomeBinding
    private val unPinnedAdapter = NoteAdapter()
    private val pinnedAdapter = NoteAdapter()
    private val colorAdapter = ColorAdapter()
    private var selectedItem: Note? = null
    private val colors by lazy(LazyThreadSafetyMode.NONE) { MutableStateFlow(CircleColorList(requireContext()).getColors()) }
    private val homeViewModel by viewModels<HomeViewModel>(::requireActivity)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.setAdapters()
        binding.setSearchbar()
        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.setHidden()
        behavior.addBottomSheetCallback(BottomSheetCallbackImpl(binding.included.underlay))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

            setSnackbar()
            setPaddings(view)
            setFab()
            setToolbarItemListener()
            included.bottomsheet.getBehavior().setHidden()
        }
    }

    private fun FragmentHomeBinding.setAdapters() {
        pinnedAdapter.listener = this@HomeFragment
        unPinnedAdapter.listener = this@HomeFragment
        colorAdapter.listener = this@HomeFragment
        included.recycler.adapter = colorAdapter
        included.recycler.itemAnimator = null
        list.adapter = ConcatAdapter(pinnedAdapter, unPinnedAdapter)
    }

    private fun FragmentHomeBinding.setToolbarItemListener() {
        toolbar.setOnMenuItemClickListener {
            searchBar.root.isVisible = true
            toolbar.menu.findItem(R.id.app_bar_search).isVisible = false
            searchBar.searchEditText.requestFocus()
            searchBar.searchIcon.isVisible = false
            val imm = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(searchBar.searchEditText, 0)
            true
        }
    }

    private fun FragmentHomeBinding.setSnackbar() {
        lifecycleScope.launch {
            homeViewModel.snackbarContent.collect {
                Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun FragmentHomeBinding.setFab() {
        add.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionCreate(-1))
        }
    }

    private fun FragmentHomeBinding.setSearchbar() {
        lifecycleScope.launch {
               homeViewModel.notes.collect{
                    it.submitLists()
               }
        }

        searchBar.cancelButton.setOnClickListener {
            toolbar.menu.findItem(R.id.app_bar_search).isVisible = true
            searchBar.searchEditText.clearFocus()
            searchBar.searchEditText.text.clear()
            searchBar.root.isVisible = false
            searchBar.searchIcon.isVisible = true
            hideKeyboard()
        }
        searchBar.searchEditText.addTextChangedListener {
            lifecycleScope.launch {
                homeViewModel.filter.emit(it.toString())
            }
        }
    }

    private fun List<Note>.submitLists() {
        val split = this.splitList()

        poop("pinned: " + split.first.size.toString())
        poop("unpinned: " + split.second.size.toString())
        pinnedAdapter.submitList(split.first)
        unPinnedAdapter.submitList(split.second)
    }

    private fun FragmentHomeBinding.setPaddings(view: View) {
        view.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val contentWidth = v.width - v.paddingLeft - v.paddingRight
            val column = contentWidth / resources.getDimensionPixelSize(R.dimen.column_min_width)
            (binding.list.layoutManager as StaggeredGridLayoutManager).spanCount = column

        }

        val padding = resources.getDimensionPixelSize(R.dimen.common_half)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
            add.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginEnd + insets.bottom
            }
            list.updatePaddingRelative(
                start = padding + insets.left,
                end = padding + insets.right,
            )
            root.updatePaddingRelative(top = insets.top)
            add.updatePaddingRelative(bottom = insets.bottom)

            windowInsets
        }
        add.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            list.updatePaddingRelative(bottom = (v.parent as View).height - v.top)
        }
    }


    private fun View.getBehavior(): BottomSheetBehavior<View> {
        val params = layoutParams as CoordinatorLayout.LayoutParams
        return params.behavior as BottomSheetBehavior
    }

    private fun BottomSheetBehavior<View>.setHidden() {
        this.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun hideKeyboard() {
        val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onClick(item: Note) {
        findNavController().navigate(HomeFragmentDirections.actionEdit(item.id))
    }

    override fun onStarClick(item: Note) {
        homeViewModel.setStared(item)
    }

    override fun onLongClick(item: Note) {
        selectedItem = item
        binding.showSelectBottomSheet()
    }


    private inner class BottomSheetCallbackImpl(private val underlay: View) : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    hideKeyboard()
                    underlay.setOnClickListener(null)
                    underlay.isClickable = false
                }

                BottomSheetBehavior.STATE_DRAGGING -> {
                    hideKeyboard()
                }

                else -> {
                    underlay.setOnClickListener {
                        val behavior = bottomSheet.getBehavior()

                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

    }

    private fun FragmentHomeBinding.showSelectBottomSheet() {
        val item = selectedItem!!
        val behavior = included.bottomsheet.getBehavior()
        colors.value = colors.value.toMutableList().setSelected(item.color)
        lifecycleScope.launch {
            colors.collect {
                colorAdapter.submitList(it)
            }
        }
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        included.clearColor.run {
            icon.setImageResource(R.drawable.ic_clear)
            title.setText(R.string.clear_color)
            root.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                homeViewModel.clearNoteColor(item)
            }
        }
        included.makeCopy.run {
            icon.setImageResource(R.drawable.ic_copy)
            title.setText(R.string.make_copy)
            root.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                homeViewModel.makeNoteCopy(item)
            }
        }
        included.edit.run {
            icon.setImageResource(R.drawable.ic_edit)
            title.setText(R.string.edit)
            root.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().navigate(HomeFragmentDirections.actionEdit(item.id))
            }
        }
        included.remove.run {
            icon.setImageResource(R.drawable.ic_delete)
            icon.imageTintList = ColorStateList.valueOf(requireContext().getThemeColor(com.google.android.material.R.attr.colorError))
            title.setText(R.string.remove)
            title.setTextColor(requireContext().getThemeColor( com.google.android.material.R.attr.colorError))
        }
        included.remove.root.setOnClickListener {
            homeViewModel.deleteNote(item)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onClick(item: CircleColor) {
        colors.value = colors.value.toMutableList().setSelected(item.color)
        homeViewModel.setNoteColor(selectedItem!!, item.color)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        super.onViewStateRestored(savedInstanceState)
    }

    private fun List<Note>.splitList(): Pair<List<Note>, List<Note>> {
        val pinnedList: MutableList<Note> = mutableListOf()
        val unPinnedList: MutableList<Note> = mutableListOf()
        for (note in this) {
            if (note.pinned) pinnedList.add(note)
            else unPinnedList.add(note)
        }
        return Pair(pinnedList, unPinnedList)
    }

    private fun MutableList<CircleColor>.setSelected(noteColor: Int): MutableList<CircleColor> {
        return this.map {
            if (it.color == noteColor) {
                it.copy(selected = true)
            } else if (it.color != noteColor && it.selected) {
                it.copy(selected = false)
            } else {
                it
            }
        }.toMutableList()
    }
}