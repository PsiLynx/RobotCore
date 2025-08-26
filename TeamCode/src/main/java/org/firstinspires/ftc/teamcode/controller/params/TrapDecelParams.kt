package org.firstinspires.ftc.teamcode.controller.params
/**
 * @param ka change in t to accelerate
 * @param kd change in t to decelerate
 * @param kf feedforward
 */
data class TrapDecelParams(
    val kd: Double = 1.0,
    val kf: Double = 0.0
){
    /**
     * @param t parametric variable
     */
    fun right(t: Double) = (t / kd) * ( 1 - kf ) + kf

}
