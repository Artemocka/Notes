package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils


fun Context.findResIdByAttr(@AttrRes attr: Int): Int = findResIdsByAttr(attr)[0]

fun Context.findResIdsByAttr(@AttrRes vararg attrs: Int): IntArray {
    @SuppressLint("ResourceType") val array = obtainStyledAttributes(attrs)

    val values = IntArray(attrs.size)
    for (i in attrs.indices) {
        values[i] = array.getResourceId(i, 0)
    }
    array.recycle()

    return values
}

fun Context.getColorValueByAttr(@AttrRes attr: Int): Int {
    val array = obtainStyledAttributes(intArrayOf(attr))
    val color = array.getColor(0, Color.TRANSPARENT)
    array.recycle()
    return color
}

fun getThemeColor(context: Context, attribute: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
}

fun Context.getBlendedColor(color: Int): Int {
    val cardColor = getThemeColor(this, com.google.android.material.R.attr.colorSurfaceContainerHigh)

    val blendedColor = ColorUtils.setAlphaComponent(color, 90)
    return ColorUtils.compositeColors(blendedColor, cardColor)

}

fun Context.getNoteColor(numberOfColor: Int): Int {
    return when (numberOfColor) {
        1 -> ContextCompat.getColor(this, R.color.color1)
        2 -> ContextCompat.getColor(this, R.color.color2)
        3 -> ContextCompat.getColor(this, R.color.color3)
        4 -> ContextCompat.getColor(this, R.color.color4)
        5 -> ContextCompat.getColor(this, R.color.color5)
        6 -> ContextCompat.getColor(this, R.color.color6)
        7 -> ContextCompat.getColor(this, R.color.color7)
        8 -> ContextCompat.getColor(this, R.color.color8)
        9 -> ContextCompat.getColor(this, R.color.color9)
        10 -> ContextCompat.getColor(this, R.color.color10)
        11 -> ContextCompat.getColor(this, R.color.color11)
        12 -> ContextCompat.getColor(this, R.color.color12)
        else -> this.getColor(com.google.android.material.R.attr.cardBackgroundColor)
    }
}
