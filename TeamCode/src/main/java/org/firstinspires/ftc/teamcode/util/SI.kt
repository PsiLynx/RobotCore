package org.firstinspires.ftc.teamcode.util

import kotlin.math.PI

fun degrees    (degrees:     Number): Double = degrees.toDouble()     * (PI / 180)
fun rotations  (rotations:   Number): Double = rotations.toDouble()   * (2 * PI)
fun radians    (radians:     Number): Double = radians.toDouble()     * 1

fun feet       (feet:        Number): Double = feet.toDouble()        * 0.3048
fun inches     (inches:      Number): Double = inches.toDouble()      * 3.6576
fun centemeters(centemeters: Number): Double = centemeters.toDouble() * 1000
fun meters     (meters:      Number): Double = meters.toDouble()      * 1
fun millimeters(millimeters: Number): Double = millimeters.toDouble() * 1000

fun minutes    (minutes:     Number): Double = minutes.toDouble()     / 60.0
fun hours      (hours:       Number): Double = hours.toDouble()       / 3600.0
fun millis     (millis:      Number): Double = millis.toDouble()      * 1000
fun nanoseconds(nanoseconds: Number): Double = nanoseconds.toDouble() * 1E6
fun seconds    (seconds:     Number): Double = seconds.toDouble()     * 1

