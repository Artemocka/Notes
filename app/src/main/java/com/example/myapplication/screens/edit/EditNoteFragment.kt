package com.example.myapplication.screens.edit

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.text.toSpanned
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditNoteBinding
import com.example.myapplication.db.Note
import com.example.myapplication.getNoteColor
import com.example.myapplication.poop
import com.example.myapplication.setIfPinned
import com.example.myapplication.viemodel.EditViewModel
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch


class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var note: Note
    private lateinit var viewModel: EditViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        val id = EditNoteFragmentArgs.fromBundle(requireArguments()).id
        poop(id.toString())
        note = if (id == -1) viewModel.currentNote.value else viewModel.getNoteFromDb(id)

        binding = FragmentEditNoteBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            viewModel.boldMode.collect {
                when (it) {
                    true -> {
                        binding.ivBold.imageTintList = ColorStateList.valueOf(MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorAccent))
                    }
                    false -> {
                        binding.ivBold.imageTintList = null
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.selectSizeMode.collect {
                when (it) {
                    true -> {
                        binding.ivFormatSize.imageTintList = ColorStateList.valueOf(MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorAccent))
                        binding.slider.isVisible = true
                    }
                    false -> {
                        binding.ivFormatSize.imageTintList = null
                        binding.slider.isVisible = false
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.underlineMode.collect {
                when (it) {
                    true -> {
                        binding.ivUnderline.imageTintList = ColorStateList.valueOf(MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorAccent))
                    }
                    false -> {
                        binding.ivUnderline.imageTintList = null
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.italicMode.collect {
                when (it) {
                    true -> {
                        binding.ivItalic.imageTintList = ColorStateList.valueOf(MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorAccent))
                    }
                    false -> {
                        binding.ivItalic.imageTintList = null
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.currentSize.collect {
                binding.slider.value = it
            }
        }



        binding.run {
            title.imeOptions = EditorInfo.IME_ACTION_NEXT
            content.imeOptions = EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE
            title.setText(note.title)
            content.setText(Html.fromHtml(note.content, Html.FROM_HTML_MODE_COMPACT))
            ivFormatSize.setOnClickListener {
                viewModel.selectSizeMode.value = !viewModel.selectSizeMode.value
            }
            setFormatButtons()
        }
        if (note.color != 0) {
            poop("note color: ${note.color}")
            val color = requireContext().getNoteColor(note.color)
            binding.appBar.setBackgroundColor(color)
            binding.root.setBackgroundColor(color)
            binding.title.setBackgroundColor(color)
            binding.content.setBackgroundColor(color)
        }

        binding.content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (viewModel.italicMode.value && viewModel.boldMode.value){
                    binding.content.text.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }else if(viewModel.italicMode.value){
                    binding.content.text.setSpan(StyleSpan(Typeface.ITALIC), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }else if (viewModel.boldMode.value){
                    binding.content.text.setSpan(StyleSpan(Typeface.BOLD), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (viewModel.underlineMode.value){
                    binding.content.text.setSpan(UnderlineSpan(), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (viewModel.currentSize.value!=1f){
                    binding.content.text.setSpan(RelativeSizeSpan(viewModel.currentSize.value), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

            }
        })

        binding.slider.addOnChangeListener{_, value, _ ->
            viewModel.setRelativeSize(binding.content, value)
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())

            binding.toolbar.updatePaddingRelative(top = systemBars.top)
            binding.linear.updatePaddingRelative(bottom = systemBars.bottom)
            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.menu.findItem(R.id.toolbar_star)?.run {
            this.icon?.setIfPinned(note.pinned, binding.root)

            this.setOnMenuItemClickListener {
                note = note.copy(pinned = !note.pinned)
                it.icon?.setIfPinned(note.pinned, binding.root)
                true
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val tempNote = Note(
            note.id,
            binding.title.text.toString().trim(),
            Html.toHtml(binding.content.text.trim().toSpanned(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL),
            note.color,
            note.pinned,
        )
        viewModel.editNote(tempNote)

    }

    private fun FragmentEditNoteBinding.setFormatButtons() {
        this.run {
            ivBold.setOnClickListener {
                viewModel.setBold(binding.content)
            }
            ivItalic.setOnClickListener {
                viewModel.setItalic(binding.content)
            }
            ivUnderline.setOnClickListener {
                viewModel.setUnderline(binding.content)
            }
            ivClearFormat.setOnClickListener {
                viewModel.clearModes(binding.content)
            }
        }
    }

}