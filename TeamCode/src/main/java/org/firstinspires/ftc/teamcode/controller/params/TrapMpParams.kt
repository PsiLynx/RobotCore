package org.firstinspires.ftc.teamcode.controller.params

import kotlin.math.pow
import kotlin.math.sqrt

data class TrapMpParams(
    val dist: Double,
    val v_max: Double,
    val v_0: Double,
    val v_f: Double,
    val a_max: Double,
    val d_max: Double = a_max,
){
    /*
    t_1 = (v_max - v_0) / a_max
    v_1(t) = a_max * t + v_0 <--

    t_2 = (
          dist
        - ( a_max / 2 * t_1^2 + v_0 * t_1 )
        - ( v_max * t_3 - d_max / 2 * t_3^2 )
    ) / v_max
    TODO: use v_{0, f} here

    v_2(t) = v_max

    t_3 = (v_max - v_f) / d_max <--
    v_3(t) = v_max - d_max * t

     */

    /**
     * @param t time since start.
     */
    fun vel(t: Double): Double {
        val t_1 = (v_max - v_0) / a_max
        val t_3 = (v_max - v_f) / d_max

        val t_2 = (
              dist
            - ( a_max / 2 * t_1.pow(2) + v_0 * t_1 )
            - ( v_max * t_3 - d_max / 2 * t_3.pow(2) )
        ) / v_max
        if(t_2 > 0){
            val t_2 = (
                  dist
                - v_max
                - v_max * (v_max / d_max) + d_max / 2 * (v_max / d_max).pow(2)
            ) / v_max
            return when {
                t < t_1             -> a_max * t + v_0
                t < t_1 + t_2       -> v_max
                t < t_1 + t_2 + t_3 -> v_max - d_max * ( t - t_2 - t_1 )

                else -> 0.0
            }
        }
        else {
            val v_peak = sqrt(
                ( 2 * dist + v_0.pow(2) / a_max + v_f.pow(2) / d_max )
                / ( 1/a_max + 1/d_max )
            )
            val t_1 = (v_peak - v_0) / a_max
            val t_3 = (v_peak - v_f) / d_max

            return when {
                t < t_1       -> a_max * t + v_0
                t < t_1 + t_3 -> v_peak - d_max * ( t - t_1 )

                else -> 0.0
            }
        }
    }

    /**
     * @param x distance from goal, [0, dist]
     */
    fun t(x: Double){

    }
}
