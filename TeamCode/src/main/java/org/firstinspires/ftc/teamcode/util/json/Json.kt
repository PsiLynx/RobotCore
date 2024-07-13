package org.firstinspires.ftc.teamcode.util.json

data class JsonObject(val data: MutableMap<String, Any> = mutableMapOf()){
    override fun toString(): String {

        val entries = data.map {
            var key = quoted(it.key)
            var value: String

            if (it.value is String) {
                value = quoted(it.value as String)
            } else {
                value = it.value.toString().indent()
            }

            return "$key : $value, "
        }

        var output = "{"

        entries.forEach { entry -> output += "\n$entry".indent() }

        return "$output\n}"
    }

    operator fun get(key: String) = data[key]!!
}

class JsonObjectBuilder {
    private val data: MutableMap<String, Any> = mutableMapOf()

    infix fun String.`is`(value: Any) {
        if(
               value is Number
            || value is Char
            || value is Boolean
        ){
            data[this] = value.toString()
        }
        else {
            data[this] = value
        }
    }

    fun jsonObject(key: String, block: JsonObjectBuilder.() -> Unit) {
        val nestedJsonObject = JsonObjectBuilder().apply(block).build()
        data[key] = nestedJsonObject
    }

    fun build(): JsonObject {
        return JsonObject(data)
    }
}

fun jsonObject(block: JsonObjectBuilder.() -> Unit): JsonObject {
    return JsonObjectBuilder().apply(block).build()
}

fun quoted(it: String): String{
    return  "\"" + it + "\""
}
fun String.indent() = this.replace("\n", "\n    ")
