package org.firstinspires.ftc.teamcode.test

import kotlin.math.min

fun assertEqual(x: Any, y:Any) {
    if(!x.equals(y)){
        throw AssertionError("x: $x != y: $y")
    }
}

fun unit() {}
fun diff(str1: String, str2: String): String{
    var output = ""
    var length = min(str1.length, str2.length)

    for( i in 0..<length){
        if(str1[i] != str2[i]) output += str1[i]
    }
    return output
}