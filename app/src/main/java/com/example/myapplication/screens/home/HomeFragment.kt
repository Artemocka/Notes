package com.example.myapplication.screens.home


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.db.Note
import com.example.myapplication.poop
import com.example.myapplication.screens.home.recycler.NoteRecyclerViewAdapter
import com.example.myapplication.screens.home.recycler.PinnedNoteAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), NoteRecyclerViewAdapter.NoteItemListener,PinnedNoteAdapter.NoteItemListener {


    private lateinit var binding: FragmentHomeBinding
    private val unPinnedAdapter = NoteRecyclerViewAdapter()
    private val pinnedAdapter = PinnedNoteAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.setHidden()
        behavior.addBottomSheetCallback(BottomSheetCallbackImpl(binding.included.underlay))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinnedAdapter.listener= this
        unPinnedAdapter.listener = this
        binding.list.adapter = ConcatAdapter(pinnedAdapter,unPinnedAdapter)
        lifecycleScope.launch {
            DatabaseProviderWrap.noteDao.getAll().collect {
                val lists = it.splitList()
                poop("$it")
//                poop("pinned: ${lists.first}")
//                poop("unpinned: ${lists.second}\n")

                pinnedAdapter.submitList(lists.first)
                unPinnedAdapter.submitList(lists.second)
            }

        }

        view.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val contentWidth = v.width - v.paddingLeft - v.paddingRight
            val column = contentWidth / resources.getDimensionPixelSize(R.dimen.column_min_width)
            (binding.list.layoutManager as StaggeredGridLayoutManager).spanCount = column

        }

        val padding = resources.getDimensionPixelSize(R.dimen.common_half)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(Type.displayCutout() or Type.systemBars())
            binding.add.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = marginEnd + insets.bottom
            }
            binding.list.updatePaddingRelative(
                start = padding + insets.left,
                top = padding + insets.top,
                end = padding + insets.right,
            )
            binding.root.updatePaddingRelative(top = insets.top)
            binding.add.updatePaddingRelative(bottom = insets.bottom)

            windowInsets
        }
        binding.add.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            binding.list.updatePaddingRelative(bottom = (v.parent as View).height - v.top)
        }
        binding.add.setOnClickListener {

            findNavController().navigate(HomeFragmentDirections.actionCreate())
        }
        binding.toolbar.setOnMenuItemClickListener {
//            binding.toolbar.isVisible = false
            binding.searchBar.root.isVisible = true
            binding.toolbar.menu.findItem(R.id.app_bar_search).isVisible = false
            binding.searchBar.searchEditText.requestFocus()
            binding.searchBar.searchIcon.isVisible = false
            val imm =
                binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding.searchBar.searchEditText, 0)

            true
        }
        binding.searchBar.cancelButton.setOnClickListener {
            binding.toolbar.menu.findItem(R.id.app_bar_search).isVisible = true
            binding.searchBar.searchEditText.clearFocus()
            binding.searchBar.searchEditText.text.clear()
            binding.searchBar.root.isVisible = false
            binding.searchBar.searchIcon.isVisible = true
            hideKeyboard()

        }

        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        view.viewTreeObserver.addOnGlobalLayoutListener {

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
        val imm =
            binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onClick(item: Note) {
        findNavController().navigate(HomeFragmentDirections.actionEdit(item.id))
    }

    override fun onStarClick(item: Note) {
        val invert = item.copy(pinned = !item.pinned)
        poop("onStarClick- $invert")
        DatabaseProviderWrap.noteDao.update(invert)
    }

    override fun onLongClick(item: Note) {

        showSelectBottomSheet(item)
    }


    private inner class BottomSheetCallbackImpl(private val underlay: View) :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            Log.e("", "onStateChanged\t$newState")
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

    private fun showSelectBottomSheet(item: Note) {
        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.skipCollapsed = true

        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.included.edit.icon.setImageResource(R.drawable.ic_edit)
        binding.included.edit.title.setText(R.string.edit)
        binding.included.edit.root.setOnClickListener {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            findNavController().navigate(HomeFragmentDirections.actionEdit(item.id))


        }

        binding.included.remove.icon.setImageResource(R.drawable.ic_delete)
        binding.included.remove.icon.imageTintList = ColorStateList.valueOf(Color.RED)
        binding.included.remove.title.setText(R.string.remove)
        binding.included.remove.root.setOnClickListener {
            DatabaseProviderWrap.noteDao.delete(item)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        setColors(item)


    }


    fun setColors(item: Note) {
        binding.included.color1.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color1)))
        binding.included.color1.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color1))
        }
        binding.included.color2.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color2)))
        binding.included.color2.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color2))
        }
        binding.included.color3.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color3)))
        binding.included.color3.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color3))
        }
        binding.included.color4.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color4)))
        binding.included.color4.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color4))
        }

        binding.included.color5.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color5)))
        binding.included.color5.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color5))
        }
        binding.included.color6.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color6)))
        binding.included.color6.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color6))
        }
        binding.included.color7.imageTintList =
            ColorStateList.valueOf(Color.parseColor(getString(R.string.color7)))
        binding.included.color7.setOnClickListener {
            DatabaseProviderWrap.noteDao.update(item.copy(color = R.color.color7))
        }
        binding.included.color7.clipToOutline = false


    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val behavior = binding.included.bottomsheet.getBehavior()
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        super.onViewStateRestored(savedInstanceState)
    }

    private fun List<Note>.splitList(): Pair<List<Note>, List<Note>> {
        poop("вызов")
        val pinnedList: MutableList<Note> = mutableListOf()
        val unPinnedList: MutableList<Note> = mutableListOf()
        for (note in this) {
            if (note.pinned)
                pinnedList.add(note)
            else
                unPinnedList.add(note)
        }
        return Pair(pinnedList,unPinnedList)
    }
}