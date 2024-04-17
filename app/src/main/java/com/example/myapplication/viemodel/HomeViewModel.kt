package com.example.myapplication.viemodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.db.Note
import com.example.myapplication.poop
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeViewModel : ViewModel() {

    init {
        poop("viewmodel init")
    }

    val filter = MutableStateFlow("")
    val notes = combine(
        DatabaseProviderWrap.noteDao.getAll(), filter
    ) { list, query ->
        if (query.isNotEmpty()) {
            list.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
        } else {
            list
        }
    }


    val snackbarContent = MutableSharedFlow<String>()
    fun makeNoteCopy(note: Note) {
        if (note.content.isNotEmpty() || note.title.isNotEmpty()) {
            DatabaseProviderWrap.noteDao.insert(note.copy(id=0))
        }
    }

    fun deleteNote(note: Note) {
        poop("deleteNote: $note.id")
        DatabaseProviderWrap.noteDao.delete(note)
    }

    fun clearNoteColor(note: Note) {
        poop("clearNoteColor: $note.id")
        DatabaseProviderWrap.noteDao.update(note.copy(color = 0))
    }

    fun setNoteColor(note: Note, color: Int) {
        poop("setNoteColor: $note.id: $color")
        DatabaseProviderWrap.noteDao.update(note.copy(color = color))
    }

    fun setStared(note: Note) {
        poop("setStared: $note.id")
        val invert = note.copy(pinned = !note.pinned)
        DatabaseProviderWrap.noteDao.update(invert)
    }

    override fun onCleared() {
        poop("onCleared")
    }

}


