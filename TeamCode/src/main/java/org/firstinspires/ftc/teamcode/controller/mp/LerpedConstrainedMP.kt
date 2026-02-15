package org.firstinspires.ftc.teamcode.controller.mp

import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.valMap
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.properties.Delegates

/**
 * @param v_0 starting velocity
 * @param v_f_target target end velocity, the only situation this will not be
 * achieved is when it is illegal to accelerate fast enough to hit this fast
 * of a speed.
 * @param a_max maximum positive acceleration
 * @param d_max maximum decceleration
 * @param velocityMaxes list of functions mapping x -> v. i.e, the velocity
 * will be no higher than the slowest value returned by any of these
 * functions at x.
 * @param ppi number of points per inch to linearly interpolate between.
 * any intermediate values will be found by linearly interpolating between
 * the values around it.
 */
class LerpedConstrainedMP(
    val v_0: Double,
    val v_f_target: Double,
    val a_max: Double,
    val d_max: Double,
    val dist: Double,
    val velocityMaxes: List<(Double) -> Double>,
    val ppi: Int = 10
) {
    val v_f get() = table.last()
    val num_points = (ppi * dist).toInt()

    val table = Array<Double>(num_points) { i -> 0.0 }
    init {
        // forwards pass
        table.indices.forEach { i ->
            table[i] = (
                if(i == 0) v_0
                else minOf(
                    sqrt(
                        table[i - 1].pow(2) + 1.0 / ppi * 2 * a_max
                    ),


                    velocityMaxes.minOf { function ->
                        function(i / ppi.toDouble())
                    }
                )
            )
        }
        // backwards pass
        table.indices.reversed().forEach { i ->
            if(abs(table[i]) < 1e-9) {
                table[i] = 1e-9
            }
            if(i == table.size - 1 ){
                if(table[i] > v_f_target){
                    table[i] = v_f_target
                }
                return@forEach
            }

            if(
                table[i] > sqrt(
                    table[i + 1].pow(2) + 1.0 / ppi * 2 * d_max
                )
            ){
                table[i] = sqrt(
                    table[i + 1].pow(2) + 1.0 / ppi * 2 * d_max
                )
                // apply deceleration limit as an acceleration limit
                // backwards through time
            }
        }
    }

    fun v(x: Double) = (
        if(x > dist - 2.0 / ppi) v_f
        else valMap(
            x,
            fromRange = Range(
                ( floor(x * ppi)    ).toDouble() / ppi,
                ( floor(x * ppi) + 1).toDouble() / ppi
            ),
            toRange = Range(
                table[ (x * ppi).toInt()     ],
                table[ (x * ppi).toInt() + 1 ]
            ),
        )
    )

    fun dvdt(x: Double) = (
        if(x > dist - 1.0 / ppi) 0.0
        else (
            table[(x * ppi).toInt () + 1] - table[(x * ppi).toInt()]
        ) * ppi * table[(x * ppi).toInt() + 1]
    )

}