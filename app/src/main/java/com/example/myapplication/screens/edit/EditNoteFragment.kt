package com.example.myapplication.screens.edit

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.databinding.FragmentEditNoteBinding
import com.example.myapplication.db.Note
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
//            binding.appBar.backgroundTintMode =PorterDuff.Mode.MULTIPLY
            binding.appBar.setBackgroundColor(note.color)
            binding.root.setBackgroundColor(note.color)
            binding.title.setBackgroundColor(note.color)
            binding.content.setBackgroundColor(note.color)


        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.toolbar.updatePaddingRelative(top = systemBars.top)
            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val tempNote = Note(
            note.id,
            binding.title.text.toString(),
            binding.content.text.toString(),
            note.color,
            false,
        )

        if (tempNote.content.isEmpty() && tempNote.title.isEmpty()) {
            DatabaseProviderWrap.noteDao.delete(note)
        } else if (tempNote.content.isNotEmpty() || tempNote.title.isNotEmpty()) {

            DatabaseProviderWrap.noteDao.update(tempNote)
        }
    }

}