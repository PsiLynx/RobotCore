package org.firstinspires.ftc.teamcode.util

import kotlin.math.abs
import kotlin.math.min
import kotlin.time.measureTimedValue

fun assertEqual(x: Any, y:Any) {
    if(!x.equals(y)){
        throw AssertionError("x: $x != y: $y")
    }
}

fun assertWithin(value: Number, epsilon: Number){
    if(abs(value.toDouble()) > epsilon.toDouble()){
        throw AssertionError("| $value | > $epsilon!")
    }
}

fun assertGreater(larger: Number, smaller: Number){
    if(larger.toDouble() <= smaller.toDouble()){
        throw AssertionError("$larger <= $smaller, first number should be larger!")
    }
}


fun unit() { }
fun diff(str1: String, str2: String): String{
    var output = ""
    val length = min(str1.length, str2.length)

    for( i in 0..<length){
        if(str1[i] != str2[i]) output += str1[i]
    }
    return output
}

