package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemGroup
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object Shooter: SubsystemGroup<Shooter>(Flywheel, Hood) {
    const val phiNoHood = 0.34906 // 20 degrees in rad

    val readyToShoot get() =
        abs(Flywheel.controller.error) / Flywheel.MAX_VEL < 0.05

    override fun update(deltaTime: Double) {

    }

    fun getVelNoHood(dist: Double): Double {

        val start  = Vector2D(cos(phiNoHood + PI/2), sin(phiNoHood + PI/2))
        val target = Vector2D(dist, 38)
        val l = target - start
        return sqrt(
            ( 9.82 * l.x.pow(2) )
            / ( l.x * sin(2*phiNoHood) - 2*l.y*cos(phiNoHood)*cos(phiNoHood))
        )
    }

    fun targetVelAngle(dist: Double): Pair<Double, Double> {
        return getVelNoHood(dist) to phiNoHood
    }

    fun shootingState(dist: () -> Double) = (
        Flywheel.runAtVelocity { getVelNoHood(dist()) }
        parallelTo Hood.setAngle { dist() }
    ) withEnd Flywheel.stop()
}