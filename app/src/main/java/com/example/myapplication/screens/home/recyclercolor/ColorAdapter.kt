package com.example.myapplication.screens.home.recyclercolor


import android.content.res.ColorStateList
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.CircleColor
import com.example.myapplication.databinding.ItemColorBinding
import com.example.myapplication.getNoteColor
import kotlin.math.min


class ColorAdapter : ListAdapter<CircleColor, ColorAdapter.ViewHolder>(ColorItemCallBack()) {

    var listener: ColorItemListener? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].color.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemColorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val viewHolder = ViewHolder(binding)

        binding.root.setOnClickListener {
            listener?.onClick(currentList[viewHolder.bindingAdapterPosition])
        }




        return viewHolder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }


    class ViewHolder(private val binding: ItemColorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CircleColor) {
            binding.run {
                circle.imageTintList = ColorStateList.valueOf(binding.root.context.getNoteColor(item.color))
                circle.clipToOutline = true
                circle.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) = outline.setRoundRect(0, 0, view.width, view.height, min(view.width, view.height).toFloat())
                }
                if (item.selected){
                    circle.setImageResource(R.drawable.ic_selected_circle)
                }else{
                    circle.setImageResource(R.drawable.ic_circle)
                }
            }
        }
    }


    interface ColorItemListener {
        fun onClick(item: CircleColor)
    }


}

