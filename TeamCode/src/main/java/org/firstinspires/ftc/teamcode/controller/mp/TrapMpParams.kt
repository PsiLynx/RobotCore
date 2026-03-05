package org.firstinspires.ftc.teamcode.controller.mp

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.Delegates

data class TrapMpParams(
    val dist: Double,
    val v_max: Double,
    val v_0: Double,
    val v_f: Double,
    val a_max: Double,
    val d_max: Double = a_max,
) {
    var t_1    by Delegates.notNull<Double>()
    var t_2    by Delegates.notNull<Double>()
    var t_3    by Delegates.notNull<Double>()
    var v_peak = v_max

    fun state(x: Double) = (
        if(t(x) < t_1) State.ACCEL
        else if(t(x) < t_1 + t_2) State.AT_SPEED
        else if(t(x) < t_1 + t_2 + t_3) State.DECCEL
        else State.UNKNOWN
    )
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
    init {
        t_1 = (v_max - v_0) / a_max
        t_3 = (v_max - v_f) / d_max

        t_2 = (
            dist
            - ( a_max / 2 * t_1.pow(2) + v_0 * t_1 )
            - ( v_max * t_3 - d_max / 2 * t_3.pow(2) )
        ) / v_max

        if(t_2 < 0) {
            v_peak = sqrt(
                (2 * dist + v_0.pow(2) / a_max + v_f.pow(2) / d_max)
                        / (1 / a_max + 1 / d_max)
            )
            t_1 = (v_peak - v_0) / a_max
            t_3 = (v_peak - v_f) / d_max

            t_2 = 0.0

        }
    }

    /**
     * @param t time since start.
     */
    fun vel(t: Double): Double {
        if(t_2 > 0){
            return when {
                t <= 0               -> v_0
                t <= t_1             -> a_max * t + v_0
                t <= t_1 + t_2       -> v_max
                t <= t_1 + t_2 + t_3 -> v_max - d_max * ( t - t_2 - t_1 )

                else                 -> v_f

            }
        }
        else {
            return when {
                t <= 0         -> v_0
                t <= t_1       -> a_max * t + v_0
                t <= t_1 + t_3 -> v_peak - d_max * ( t - t_1 )

                else           -> v_f
            }
        }
    }

    /**
     * @param x distance from start, [0, dist]
     */
    fun t(x: Double): Double {
        val x_1 = 1.0/2 * a_max * t_1.pow(2) + v_0 * t_1
        val x_2 = v_max * t_2
        return when {
            x <= x_1 -> (
                ( -v_0 + sqrt(v_0.pow(2) + 2 * a_max * x))
                / a_max
            )

            x <= x_1 + x_2 -> (
                ( x - x_1 )
                / v_max
            ) + t_1

            x <= dist -> (
                t_1 + t_2 + (
                    v_peak - sqrt(
                        v_peak.pow(2) - 2 * d_max * (x - x_1 - x_2)
                    )
                ) / d_max
            )

            else -> 0.0
        }

    }

    /**
     * @param x distance from goal, [0, dist]
     */
    fun velFromX(x: Double) = vel(t(x))

    enum class State{
        ACCEL, AT_SPEED, DECCEL, UNKNOWN
    }
}