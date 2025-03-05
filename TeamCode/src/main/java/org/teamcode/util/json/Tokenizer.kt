package org.teamcode.util.json

fun tokenize(str: String): JsonObject {
    var json = str.sanitize().drop(1)
    return jsonObject {
        while(json.isNotEmpty()) {
            when (json[0]) {
                '}' -> json = json.drop(1)
                ',' -> json = json.drop(1)
                ' ' -> json = json.drop(1)
                '"' -> {
                    val key = json.value() as String
                    json = json.drop("\"$key\":".length)

                    val value = json.value()
                    json = json.drop(
                        when (value) {
                            is Number, Boolean, Char -> quoted(value.toString())
                            is String -> quoted(value)
                            else -> value.toString()
                        }.sanitize()
                        .length
                    )

                    key `is` value
                }
                else -> throw IllegalStateException("json tokenizer cannot read next character \"${json[0]}\" in |<$json>|")
            }
        }
    }
}

fun String.findClosing(char: Char, pos: Int = 0): Int{
    var level = 0
    when(char){
        '"' -> return this.indexOf('"', pos + 1)
        '{' -> for(i in pos..<this.length){
            when(this[i]){
                '{' -> level ++
                '}' -> {
                    level --
                    if(level == 0) return i
                }
            }
        }
        '[' -> for(i in pos..<this.length){
            when(this[i]){
                '[' -> level ++
                ']' -> {
                    level --
                    if(level == 0) return i
                }
            }
        }
    }
    return -1
}
fun String.value(pos: Int = 0): Any{
    return when(this[pos]) {
        '"' -> this.substring(
            pos + 1,
            this.findClosing('"', pos)
        )
        '{' -> tokenize(
            this.substring(
                pos,
                this.findClosing('{', pos)
            )
        )
        '[' -> JsonList(
            this.substring(
                pos + 1,
                this.findClosing('[')
            ).splitList()
        )

        else -> throw IllegalStateException("character at $pos in $this is not \", {, or [; cannot get a value")
    }
}
fun String.removeTabs(): String{
    return this.replace("    ", "")
}

fun String.splitList(): List<Any>{
    val list = arrayListOf<Any>()

    var str = this
    while(str.isNotEmpty()){

        val value = str.value()
        list.add(value)
        str = str.drop(
            (
                when (value) {
                    is Number, Boolean, Char -> quoted(value.toString())
                    is String -> quoted(value)
                    else -> value.toString()
                }.sanitize() + ","
            ).length
        )
    }
    return list
}
fun String.sanitize() =
    this
        .removeTabs()
        .replace("\n", "")
        .replace(": ", ":")
        .replace(" :", ":")
        .replace(" : ", ":")
        //.replace(", ", ",")
