package com.example.myapplication

import android.content.Context
import androidx.room.Room
import com.example.myapplication.db.DatabaseProvider
import com.example.myapplication.db.NoteDao

object DatabaseProviderWrap {

    const val VERSION = DatabaseProvider.VERSION
    private lateinit var provider: DatabaseProvider


    val noteDao: NoteDao get() = provider.dao

    fun closeDao() = provider.close()


    fun createDao(context: Context) {
        provider = Room.databaseBuilder(context, DatabaseProvider::class.java, "notes")
            .allowMainThreadQueries()
            .build()
    }
}