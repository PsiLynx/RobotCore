package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs

/**
 * param data: an arraylist of pairs. <p>
 * each pair has a pair of [applied voltage and current velocity] and acceleration
 */
class SimulatedMotor(var data: ArrayList<MotorDataPoint>): FakeMotor() {
    private var possibleVoltages = arrayListOf<Double>()
    override var maxVelocityInTicksPerSecond =
        data.maxBy { it.velocity }.velocity.toInt()

    init {
        for(dataPoint in data){
            val voltage = dataPoint.voltage
            if( voltage !in possibleVoltages ){
                possibleVoltages.add(voltage)
            }
        }


    }
    override fun update(deltaTime: Double) {
        val voltage = closestVoltageTo( Globals.robotVoltage * this.power )
        val velocity = this.speed * maxVelocityInTicksPerSecond

        val closestDataPoint = (
                data
                    .filter { it.voltage == voltage }
                    .minBy  { it.velocity - velocity }
                )

        val acceleration = closestDataPoint.acceleration

        speed += acceleration * deltaTime
    }

    private fun closestVoltageTo(v: Double) = possibleVoltages.minBy { abs(v - it) }
}