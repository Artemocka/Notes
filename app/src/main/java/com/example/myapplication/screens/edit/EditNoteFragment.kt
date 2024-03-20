package com.example.myapplication.screens.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditNoteBinding
import com.example.myapplication.findResIdByAttr


class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val id = EditNoteFragmentArgs.fromBundle(requireArguments()).id
        val note = DatabaseProviderWrap.noteDao.getById(id.toLong())
        binding = FragmentEditNoteBinding.inflate(layoutInflater)


        binding.run {
            title.setText(note.title)
            content.setText(note.content)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

            binding.toolbar.updatePaddingRelative(top = systemBars.top)

            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.menu.findItem(R.id.app_bar_save).setOnMenuItemClickListener {
            val tempNote = com.example.myapplication.db.Note(
                note.id,
                binding.title.text.toString(),
                binding.content.text.toString(),
                note.color,
                false,
            )

            if (tempNote.content.isNotEmpty()){
                DatabaseProviderWrap.noteDao.update(tempNote)
            }

            findNavController().popBackStack()

            true
        }

        return  binding.root
    }


}