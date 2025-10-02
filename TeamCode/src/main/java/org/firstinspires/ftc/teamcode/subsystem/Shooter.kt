package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemGroup

object Shooter: SubsystemGroup<Shooter>(Flywheel, Hood) {
    override fun update(deltaTime: Double) {

    }

    /*
    fun getVel(phi: Double, dist: Double): Double {
        val start = Vector2D(cos(phi + PI/2), sin(phi + PI/2))
    }

    fun targetVelAngle(dist: Double): Pair<Double, Double> {
        val targetVel = sqrt(
            -385.82
        )
    }

    fun shoot(dist: Double) = (
        Flywheel.()
        parallelTo Hood.setAngle()
    )
     */
}