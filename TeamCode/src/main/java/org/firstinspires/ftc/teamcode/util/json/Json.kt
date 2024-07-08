package org.firstinspires.ftc.teamcode.util.json

data class JsonObject(val data: MutableMap<String, Any> = mutableMapOf()){
    override fun toString(): String {
        var entries = data.map {
            (
                    it.key.quote()
                    + " : "
                    + if(it.value is String) (it.value as String).quote()
                    else it.value.toString().replace("\n", "\n    ")
            )
        }

        var output = "{"
        for( entry in entries ){
            output += "\n$entry".replace("\n", "\n    ")
        }
        return "$output\n}"
    }
}

class JsonObjectBuilder {
    private val data: MutableMap<String, Any> = mutableMapOf()

    infix fun String.`is`(value: Any) {
        data[this] = value
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

fun String.quote(): String{
    return  "\"" + this + "\""
}
