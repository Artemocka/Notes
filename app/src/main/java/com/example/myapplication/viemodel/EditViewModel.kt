package com.example.myapplication.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.db.Note
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditViewModel : ViewModel() {
    val snackbarContent = MutableSharedFlow<String>()
    val currentNote =MutableStateFlow<Note>(Note(0,"","",0,false))
    var formatMode = MutableStateFlow(false)

    val boldMode = MutableStateFlow(false)
    val italicMode = MutableStateFlow(false)
    val underlineMode = MutableStateFlow(false)

    fun editNote(note: Note) {
        if (note.id == 0) {
            if (note.content.isNotEmpty() || note.title.isNotEmpty()) {
                DatabaseProviderWrap.noteDao.insert(note)
            }
        } else {
            if (note.content.isEmpty() && note.title.isEmpty()) {
                DatabaseProviderWrap.noteDao.delete(note)
                viewModelScope.launch {
                    snackbarContent.emit("Note was deleted")
                }
            } else {
                DatabaseProviderWrap.noteDao.update(note)
            }
        }
    }

    fun setStarred(){
        val isStar = currentNote.value.pinned
        currentNote.value = currentNote.value.copy(pinned = !isStar)
    }

    fun getNoteFromDb(id: Int): Note {
        currentNote.value = DatabaseProviderWrap.noteDao.getById(id.toLong())
        return currentNote.value
    }

    fun clearModel() {
        currentNote.value = Note(0,"","",0,false)
        formatMode.value = false
    }
}