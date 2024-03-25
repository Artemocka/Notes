package com.example.myapplication.data

import android.content.Context

data class CircleColorList(val context: Context) {
    private val list = listOf(
        CircleColor(1),
        CircleColor(2),
        CircleColor(3),
        CircleColor(4),
        CircleColor(5),
        CircleColor(6),
        CircleColor(7),
        CircleColor(8),
        CircleColor(9),
        CircleColor(10),
        CircleColor(11),
        CircleColor(12),
    )

    fun getColors(): List<CircleColor> {
        return list
    }

    fun getSelected(noteColor: Int): List<CircleColor> {
        return list.map {
            if (it.color == noteColor) it.copy(selected = true)
            else it
        }.toList()
    }
}
