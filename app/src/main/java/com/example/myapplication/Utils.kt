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
