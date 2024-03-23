package com.example.myapplication.data

data class CircleColorList(var list:MutableList<CircleColor>) {
    init {
        list = mutableListOf<CircleColor>(
            CircleColor(-8972498),
            CircleColor(-9884905),
            CircleColor(-14138795),
            CircleColor(-14326921),
            CircleColor(-15965603),
            CircleColor(-14267077),
            CircleColor(-12112293),
            CircleColor(-9684657),
            CircleColor(-11844550),
            CircleColor(-14474201),
        )
    }

    fun getSelected(noteColor:Int):List<CircleColor>{
        return list.map {
            if (it.color == noteColor) {
                it.copy(selected = true)
            } else if (it.color != noteColor && it.selected) {
                it.copy(selected = false)
            } else {
                it
            }

        }.toList()
    }
}