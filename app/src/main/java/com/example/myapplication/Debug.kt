package com.example.myapplication

import android.util.Log

fun Any.poop(s:String){
    Log.e("NoteApp","[${javaClass.simpleName}] $s")
}