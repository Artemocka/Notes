package com.example.myapplication.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.DatabaseProviderWrap
import com.example.myapplication.db.Note
import com.example.myapplication.poop
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    init {
        poop("viewmodel init")
    }
    val notes=DatabaseProviderWrap.noteDao.getAll()
    val snackbarContent = MutableSharedFlow<String>()
    fun createNote(note: Note){
        if (note.content.isNotEmpty()||note.title.isNotEmpty()) {
            DatabaseProviderWrap.noteDao.insert(note)
        }
    }

    fun editNote(note: Note){
        if (note.content.isEmpty() && note.title.isEmpty()) {
            DatabaseProviderWrap.noteDao.delete(note)
            viewModelScope.launch {
                snackbarContent.emit("Note was deleted")
            }
        } else {
            DatabaseProviderWrap.noteDao.update(note)
        }
    }

    fun deleteNote(note: Note){
        poop("deleteNote: $note.id")
        DatabaseProviderWrap.noteDao.delete(note)
    }

    fun clearNoteColor(note: Note) {
        poop("clearNoteColor: $note.id")
        DatabaseProviderWrap.noteDao.update(note.copy(color = 0))
    }
    fun setNoteColor(note:Note, color:Int){
        poop("setNoteColor: $note.id: $color")
        DatabaseProviderWrap.noteDao.update(note.copy(color = color))
    }
    fun setStared(note: Note){
        poop("setStared: $note.id")
        val invert = note.copy(pinned = !note.pinned)
        DatabaseProviderWrap.noteDao.update(invert)
    }

    override fun onCleared() {
        poop("onCleared")
    }

}


