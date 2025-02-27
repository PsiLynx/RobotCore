package org.ftc3825.util.control

data class MotionProfileParameters(
    val ka: Double = 1.0,
    val kd: Double = 1.0,
    val kf: Double = 0.1
){
    fun left(t: Double) = (t * ka).coerceIn(kf, 1.0)
    fun right(t: Double) = ( -t * kd ).coerceIn(kf, 1.0)

}