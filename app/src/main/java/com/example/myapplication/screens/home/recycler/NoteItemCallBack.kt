package com.example.myapplication.screens.home.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.db.Note

class NoteItemCallBack : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}