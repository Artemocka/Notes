package com.example.myapplication.screens.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.databinding.FragmentEditNoteBinding
import com.example.myapplication.findResIdByAttr


class CreateNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(layoutInflater)



        binding.run {
            //EdiText's
            title.imeOptions = EditorInfo.IME_ACTION_NEXT
            content.imeOptions = EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE

            //toolbar
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }


            //insets
            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

                binding.toolbar.updatePaddingRelative(top = systemBars.top)

                insets
            }
        }




        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val note = com.example.myapplication.db.Note(
            0,
            binding.title.text.toString(),
            binding.content.text.toString(),
            requireContext().findResIdByAttr(androidx.cardview.R.attr.cardBackgroundColor),
            false
        )

        if (note.content.isNotEmpty()||note.title.isNotEmpty()) {
            DatabaseProviderWrap.noteDao.insert(note)
        }
    }


}

