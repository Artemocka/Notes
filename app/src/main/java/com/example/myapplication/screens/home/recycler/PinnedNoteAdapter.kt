package com.example.myapplication.screens.home.recycler

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentItemBinding
import com.example.myapplication.db.Note
import com.example.myapplication.findResIdByAttr
import com.example.myapplication.poop

class PinnedNoteAdapter :
    ListAdapter<Note, PinnedNoteAdapter.ViewHolder>(NoteItemCallBack()) {

    var listener: NoteItemListener? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = FragmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
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
            listener?.onStarClick(currentList[viewHolder.bindingAdapterPosition])
        }




        return viewHolder

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)

    }


    class ViewHolder(private val binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }

        fun bind(item: Note) {
            binding.run {

                title.text = item.title
                note.text = item.content

                if (item.color != 0) {

                    val accentColor = ContextCompat.getColor(this.root.context, item.color)

                    val cardColor =
                        this.root.context.findResIdByAttr(androidx.cardview.R.attr.cardBackgroundColor)
                    val alpha = ColorUtils.setAlphaComponent(accentColor, 50)

                    root.setCardBackgroundColor(
                        ColorUtils.compositeColors(alpha, cardColor)
                    )
                }

                if (item.pinned)
                    pin.imageTintList = ColorStateList.valueOf(Color.YELLOW)
                else {
                    poop("else")
                    pin.imageTintList = ColorStateList.valueOf(Color.WHITE)
                }

            }
        }

    }


    interface NoteItemListener {
        fun onClick(item: Note)
        fun onStarClick(item:Note)
        fun onLongClick(item: Note)
    }


}