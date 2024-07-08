package org.firstinspires.ftc.teamcode.util.json

const val space = ' '
const val newLine = '\n'

fun tokenize(json: String): JsonObject {
    var json = json.removeTabs().replace("\n", "")
    return jsonObject {
        json = json.eat(1)
        while(json.length > 0) {
            when (json[0]) {
                '}' -> json = json.eat(1)
                '"' -> {
                    val key = json.value() as String
                    json = json.eat(key.length + 5)// eat key, '"', '"', and ' : '
                    val value = json.value()
                    json = json.eat(
                        value
                            .toString()
                            .removeTabs()
                            .length
                                + 2
                    ) // eat '"' value '"'

                    key `is` value
                }
            }
        }
    }
}

fun String.eat(num: Int): String{
    if(num < this.length) {
        return this.substring(num)
    }
    return ""
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
                this
                    .substring(1, this.length - 2)//remove ',]'
                    .splitList()
            )

        else -> throw IllegalStateException("character at $pos in $this is not \", {, or [; cannot get a value")
    }
}
fun String.removeTabs(): String{
    /*var i = 0
    var quoted = false
    var str = this
    while(i < str.length){
        if(str[i] == '"'){
            quoted = !quoted
            i ++
        }
        else if(str[i] == ' ' && !quoted) {
            str = str.remove(i)
        }
        else{
            i ++
        }
    }
    return str*/
    return this.replace("    ", "")
}

fun String.remove(i: Int): String{
    return when(i) {
        0 -> this.substring(1)
        this.length - 1 -> this.substring(0, this.length-1)
        else -> this.substring(0, i)+this.substring(i+1, this.length)
    }
}
fun String.splitList(): List<Any>{
    val list = arrayListOf<Any>()

    var str = this
    while(str.isNotEmpty()){

        list.add(str.value())
        str = str.eat(str.value().toString().length + 3)
    }
    return list
}