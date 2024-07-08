package org.firstinspires.ftc.teamcode.test

fun assertEqual(x: Any, y:Any) {
    if(x != y){
        throw AssertionError("x: $x != y: $y")
    }
}

fun unit() {}