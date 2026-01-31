package org.firstinspires.ftc.teamcode.geometry

fun valMap(fromVal: Double, fromRange: Range, toRange: Range): Double{
    //comp percent fromVal is in fromRamge:
    val percent = (fromVal - fromRange.start) / fromRange.size()
    return percent * toRange.size() + toRange.start
}