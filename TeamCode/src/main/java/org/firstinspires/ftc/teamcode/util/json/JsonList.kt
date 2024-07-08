package org.firstinspires.ftc.teamcode.util.json

class JsonList<E>(arrayList: List<E>): ArrayList<E>(), List<E> {
    init{
        arrayList.map { this.add(it) }
    }
    override fun toString(): String {
        var output = "[\n"
        for (it in this) {
            output += (if (it is String) it.quote() else it.toString()) + ",\n"
        }

        return "$output]"
    }


}