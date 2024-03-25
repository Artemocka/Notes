package com.example.myapplication.screens.edit

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditNoteBinding
import com.example.myapplication.db.Note
import com.example.myapplication.getNoteColor
import com.example.myapplication.getThemeColor
import com.example.myapplication.poop


class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private lateinit var note: Note

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val id = EditNoteFragmentArgs.fromBundle(requireArguments()).id
        note = DatabaseProviderWrap.noteDao.getById(id.toLong())
        binding = FragmentEditNoteBinding.inflate(layoutInflater)

        binding.run {
            title.setText(note.title)
            content.setText(note.content)
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
            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }




        binding.toolbar.menu.findItem(R.id.toolbar_pin)?.run {
            this.icon?.setIfPinned(note.pinned)

            this.setOnMenuItemClickListener {
                note = note.copy(pinned = !note.pinned)
                it.icon?.setIfPinned(note.pinned)
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
            binding.content.text.toString().trim(),
            note.color,
            note.pinned,
        )

        if (tempNote.content.isEmpty() && tempNote.title.isEmpty()) {
            DatabaseProviderWrap.noteDao.delete(note)
        } else {

            DatabaseProviderWrap.noteDao.update(tempNote)
        }
    }


    private fun Drawable.setColor(color: Int) {
        this.setTintList(ColorStateList.valueOf(color))
    }

    private fun Drawable.setIfPinned(pinned: Boolean) {
        val starColor = getThemeColor(binding.root.context, com.google.android.material.R.attr.colorAccent)

        if (pinned) {
            this.setColor(starColor)
        } else {
            val blended = ColorUtils.setAlphaComponent(starColor, 50)
            this.setColor(blended)
        }
    }


}