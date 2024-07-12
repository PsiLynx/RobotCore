package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs

/**
 * param data: an arraylist of pairs. <p>
 * each pair has a pair of [applied voltage and current velocity] and acceleration
 */
class SimulatedMotor(var data: ArrayList<Pair<Pair<Double, Double>, Double>>): FakeMotor() {
    var possibleVoltages = arrayListOf<Double>()
    init {
        for(pair in data){
            val voltage = pair.first.first
            if( voltage !in possibleVoltages ){
                possibleVoltages.add(voltage)
            }
        }

        maxVelocityInTicksPerSecond = data.maxBy { it.first.second}.first.second.toInt()
    }
    override fun update(deltaTime: Double) {
        val voltage = closestVoltageTo( Globals.robotVoltage * this.power )
        val velocity = this.speed * maxVelocityInTicksPerSecond

        val closestDataPoint = (data
            .filter { it.first.first == voltage }
            .minBy { it.first.second - velocity }
                )

        val acceleration = closestDataPoint.second

        speed += acceleration * deltaTime
    }

    fun closestVoltageTo(v: Double) = possibleVoltages.minBy { abs(v - it) }
}