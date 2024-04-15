package com.example.myapplication.screens.home.recycler


import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentItemBinding
import com.example.myapplication.db.Note
import com.example.myapplication.getNoteColor
import com.example.myapplication.poop
import com.google.android.material.color.MaterialColors


class NoteAdapter : ListAdapter<Note, NoteAdapter.ViewHolder>(NoteItemCallBack()) {

    var listener: NoteItemListener? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val viewHolder = ViewHolder(binding)

        binding.root.setOnClickListener {
            listener?.onClick(currentList[viewHolder.bindingAdapterPosition])
        }
        binding.root.setOnLongClickListener {
            listener?.onLongClick(currentList[viewHolder.bindingAdapterPosition])

            true
        }
        binding.pin.setOnClickListener {
            if (viewHolder.bindingAdapterPosition >= 0) {
                val pinned = currentList[viewHolder.bindingAdapterPosition]
                listener?.onStarClick(pinned)
            }
        }



        return viewHolder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)


    }


    class ViewHolder(private val binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note) {
            binding.run {

                if (item.title.isEmpty()) {
                    title.isVisible = false
                } else {
                    title.isVisible = true
                    title.text = item.title

                }
                note.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_COMPACT)

                if (item.color != 0) {
                    val noteColor = root.context.getNoteColor(item.color)
                    root.setCardBackgroundColor(noteColor)
                } else {
                    val cardColor =  MaterialColors.getColor(root, com.google.android.material.R.attr.colorSurfaceContainerHigh)
                    poop(cardColor.toString())
                    root.setCardBackgroundColor(cardColor)

                }

                val starColor = MaterialColors.getColor(root, com.google.android.material.R.attr.colorAccent)

                if (item.pinned) {
                    pin.imageTintList = ColorStateList.valueOf(starColor)
                    pin.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                } else {
                    val alpha = ColorUtils.setAlphaComponent(starColor, 50)
                    pin.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                    pin.imageTintList = ColorStateList.valueOf(alpha)

                }
            }
        }


    }


    interface NoteItemListener {
        fun onClick(item: Note)
        fun onStarClick(item: Note)
        fun onLongClick(item: Note)
    }


}

//package org.example
//
//import kotlin.math.max
//
//
//fun main() {
//
//    val marks: MutableList<Int> = mutableListOf()
//
//    val numOfMarks = readln().toInt()
//    val listOfMarks = readln().split(" ").map(String::toInt).toMutableList()
//    var left = 0
//    var right = 1
//    var maxLength = 0
//    var tempRight =1
//   while (right!=numOfMarks){
//       tempRight = right
//       while (right != numOfMarks) {
//
//           if (listOfMarks.onlyGooddMarks(left, right))
//               maxLength = right-left
//           else
//               left+=1
//           right+=1
//       }
//       right= tempRight+1
//       left=0
//   }
//    println(maxLength)
//
//}
//
fun MutableList<Int>.onlyGooddMarks(start: Int, end: Int): Boolean {

    for (i in start..end) {
        if (this[i]==2|| this[i]==3)
            return false
    }
    return true
}

