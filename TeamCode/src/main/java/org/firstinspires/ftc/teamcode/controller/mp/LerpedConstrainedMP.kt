package org.firstinspires.ftc.teamcode.controller.mp

import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.valMap
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.Delegates

/**
 * @param v_0 starting velocity
 * @param v_f_target target end velocity, the only situation this will not be
 * achieved is when it is illegal to accelerate fast enough to hit this fast
 * of a speed.
 * @param a_max maximum positive acceleration
 * @param d_max maximum decceleration
 * @param velocityMaxes list of functions mapping t -> v. i.e, the velocity
 * will be no higher than the slowest value returned by any of these
 * functions at any point.
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
    val num_points = (ppi * dist).toInt() + 1

    val table = Array<Double>(num_points) { i ->
        minOf(
            sqrt(v_0.pow(2) + 2 * a_max * i / ppi), // v(x)

            velocityMaxes.minBy { function ->
                function(i / ppi.toDouble())
            }.invoke(i / ppi.toDouble())
        )
    }
    init {
        // backwards pass
        table.indices.reversed().forEach { i ->
            if(i == table.size - 1 ){
                if(table[i] > v_f_target){
                    table[i] = v_f_target
                }
                return@forEach
            }

            if(
                (table[i] - table[i + 1]) / ppi > d_max / table[i]
            ){
                table[i] = sqrt(
                    v_f.pow(2)
                    + 2 * d_max * ( table.size - i - 1 )/ppi
                )
                // apply deceleration limit as an acceleration limit
                // backwards through time
            }
        }
    }

    fun v(x: Double) = valMap(
        x,
        fromRange = Range(
            ( (x * ppi).toInt()    ) / ppi,
            ( (x * ppi).toInt() + 1) / ppi
        ),
        toRange = Range(
            table[ (x * ppi).toInt()     ],
            table[ (x * ppi).toInt() + 1 ]
        ),
    )


}