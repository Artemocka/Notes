package com.example.myapplication.screens.edit

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.getSpans
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
import kotlinx.coroutines.launch


class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var note: Note
    private lateinit var viewModel :EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        val id = EditNoteFragmentArgs.fromBundle(requireArguments()).id
        poop(id.toString())
        note = if (id == -1) viewModel.currentNote.value else viewModel.getNoteFromDb(id)

        binding = FragmentEditNoteBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            viewModel.formatMode.collect {
                binding.ivFormatMode.isVisible = !it
            }
        }

        binding.run {
            title.setText(note.title)
            content.setText(Html.fromHtml(note.content,Html.FROM_HTML_MODE_COMPACT))

            ivFormatMode.setOnClickListener {
                viewModel.formatMode.value = !viewModel.formatMode.value
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



        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.toolbar.updatePaddingRelative(top = systemBars.top)
            binding.ivFormatMode.updatePaddingRelative(bottom = systemBars.bottom)
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
            Html.toHtml(binding.content.text.trim().toSpanned(),Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL ),
            note.color,
            note.pinned,
        )

        viewModel.editNote(tempNote)

    }

    fun FragmentEditNoteBinding.setFormatButtons(){
        this.run {
            ivBold.setOnClickListener {
                if (content.hasChars()) {
                    content.text.setSpan(
                        StyleSpan(Typeface.BOLD), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            ivItalic.setOnClickListener {
                if (content.hasChars()) {
                    content.text.setSpan(
                        StyleSpan(Typeface.ITALIC), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            ivUnderline.setOnClickListener {
                poop(content.selectionStart.toString()+"  "+content.selectionEnd.toString())
                if (content.selectionStart!=content.selectionEnd){
                    if (content.hasChars()) {
                        content.text.setSpan(
                            UnderlineSpan(), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }else{
                    content.text.setSpan(
                        UnderlineSpan(), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
            }
            ivClearFormat.setOnClickListener{
                if (content.hasChars()) {
                    val spans =  content.text.getSpans<Any>(content.selectionStart,content.selectionEnd)
                    for (span in spans){
                        when(span){
                            is StyleSpan->content.text.removeSpan(span)
                            is UnderlineSpan->content.text.removeSpan(span)
                        }
                    }
                }
            }
        }
    }


    fun EditText.hasChars(): Boolean {
        for (i in text.subSequence(selectionStart, selectionEnd)) {
            if (i.isLetter()) return true
        }
        return false
    }


}