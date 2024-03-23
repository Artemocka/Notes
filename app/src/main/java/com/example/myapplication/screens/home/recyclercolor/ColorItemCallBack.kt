package com.example.myapplication.screens.home.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.data.CircleColor

class ColorItemCallBack : DiffUtil.ItemCallback<CircleColor>() {
    override fun areItemsTheSame(oldItem: CircleColor, newItem: CircleColor): Boolean {
        return oldItem.color == newItem.color
    }

    override fun areContentsTheSame(oldItem: CircleColor, newItem: CircleColor): Boolean {
        return oldItem == newItem
    }

}