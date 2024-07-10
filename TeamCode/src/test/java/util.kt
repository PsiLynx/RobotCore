package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.util.millis
import org.firstinspires.ftc.teamcode.util.nanoseconds
import kotlin.math.abs
import kotlin.math.min
import kotlin.time.measureTimedValue

fun assertEqual(x: Any, y:Any) {
    if(!x.equals(y)){
        throw AssertionError("x: $x != y: $y")
    }
}

fun assertWithin(value: Number, max: Number){
    if(abs(value.toDouble()) > max.toDouble()){
        throw AssertionError("|$value| > $max!")
    }
}

fun assertTakes(seconds: Number, epsilon: Number = 1e-2, code: () -> Any){
    var time = millis(
        measureTimedValue {
            code()
        }.duration.inWholeMilliseconds
    )

    if(time - seconds.toDouble() * 1000 > epsilon.toDouble()){
        throw AssertionError("took $time, should have been within $epsilon seconds of $time")
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

fun sleep(seconds: Number){
    var startTime = System.nanoTime()
    while(
        nanoseconds( System.nanoTime() - startTime )
        < seconds.toDouble()
    ){ }
}