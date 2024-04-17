package com.example.myapplication.viemodel

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import androidx.core.text.getSpans
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
    var selectSizeMode = MutableStateFlow(false)
    val boldMode = MutableStateFlow(false)
    val italicMode = MutableStateFlow(false)
    val underlineMode = MutableStateFlow(false)
    var currentSize = MutableStateFlow(1f)

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


    fun setUnderline(content: EditText){
        if (content.selectionStart != content.selectionEnd) {
            content.text.setSpan(UnderlineSpan(), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            underlineMode.value = !underlineMode.value
        } else {
            underlineMode.value = !underlineMode.value
        }
    }
    fun setBold(content: EditText){
        if (content.selectionStart != content.selectionEnd) {
            content.text.setSpan(StyleSpan(Typeface.BOLD), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            underlineMode.value = !underlineMode.value
        } else {
            boldMode.value = !boldMode.value
        }
    }
    fun setItalic(content: EditText){
        if (content.selectionStart != content.selectionEnd) {
            content.text.setSpan(UnderlineSpan(), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            underlineMode.value = !underlineMode.value
        } else {
            italicMode.value = !italicMode.value
        }
    }
    fun setForeground(content: EditText, selectedColor:Color){
        TODO()
    }
    fun setRelativeSize(content: EditText, relativeSize:Float){
        if (content.selectionStart != content.selectionEnd) {
            content.text.setSpan(RelativeSizeSpan(currentSize.value), content.selectionStart, content.selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            currentSize.value = relativeSize
        }
    }

    fun clearModes(content: EditText) {
        if (content.selectionStart != content.selectionEnd) {
            val spans = content.text.getSpans<Any>(content.selectionStart, content.selectionEnd)
            for (span in spans) {
                when (span) {
                    is StyleSpan -> content.text.removeSpan(span)
                    is UnderlineSpan -> content.text.removeSpan(span)
                    is ForegroundColorSpan-> content.text.removeSpan(span)
                }
            }
        } else {
            underlineMode.value = false
            italicMode.value = false
            boldMode.value = false
        }
    }

}