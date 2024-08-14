package org.ftc3825.util.json

class JsonList<E>(arrayList: List<E>): ArrayList<E>(), List<E> {
    val indicies: Sequence<Int>
        get() = generateSequence(0) { if( it < this.size - 1) it + 1 else null }

    init{
        arrayList.forEach { this.add(it) }
    }
    override fun toString(): String {
        var output = "[\n"
        for (it in this) {
            output += when(it){
                is Number  -> quoted(it.toString())
                is Char    -> quoted(it.toString())
                is Boolean -> quoted(it.toString())
                is String  -> quoted(it)
                else -> it.toString()
            } + ",\n"
        }

        return "$output]"
    }


}