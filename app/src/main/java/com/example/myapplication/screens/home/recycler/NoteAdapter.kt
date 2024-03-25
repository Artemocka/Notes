package com.example.myapplication.screens.home.recycler


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentItemBinding
import com.example.myapplication.db.Note
import com.example.myapplication.getNoteColor
import com.example.myapplication.getThemeColor


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
                note.text = item.content

                val cardColor = getThemeColor(this.root.context, com.google.android.material.R.attr.colorSurfaceContainerHigh)
                if (item.color != 0) {
                    root.setCardBackgroundColor(
                        root.context.getNoteColor(item.color)
                    )
                } else {
                    root.setCardBackgroundColor(cardColor)
                }

                val starColor = getThemeColor(this.root.context, com.google.android.material.R.attr.colorAccent)

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

