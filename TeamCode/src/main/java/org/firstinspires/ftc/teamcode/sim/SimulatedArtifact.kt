package org.firstinspires.ftc.teamcode.sim

import android.R.attr.top
import org.firstinspires.ftc.teamcode.geometry.Prism3D
import org.firstinspires.ftc.teamcode.geometry.Shape3D
import org.firstinspires.ftc.teamcode.geometry.Sphere3D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.Logger
import kotlin.collections.map
import kotlin.collections.plus

/**
 * represents an Artifact ( the game element )
 * @param pos starting position
 * @param vel starting velocity
 * @param interacts a list of objects the ball can interact with
 */
data class SimulatedArtifact(
    override var pos: Vector3D,
    var vel: Vector3D,
    val interacts: MutableList<Prism3D>
): Sphere3D(pos, 2.5) {

    /**
     * elasticity of collision, [0, 1]
     */
    val e = 0.5

    fun update(deltaTime: Double){
        vel += Vector3D(0, 0, -386.088) * deltaTime
        val newpos = pos + vel * deltaTime

        val collisions = interacts.map {
            it.intersectingFaces(this).filter {
                (-it.normal) dot vel > 0
                // ensures that the velocity vector is facing away from the
                // normal, i.e we are colliding with this from the outside,
                // rather than the inside
            }.minByOrNull {
                (it.closestPoint(newpos) - newpos).mag
            }
        }.filter { it != null }. map { it!! }
        collisions.forEach { hit ->
            vel -= (
                hit.normal
                * (vel dot hit.normal)
                * ( 1 + e )
                * if(vel.mag < 10) 0.5 else 1.0
            )

//            if(vel.mag < 2){
//                val closest = hit.closestPoint(pos)
//                val dist = (pos - closest).mag
//                if(dist < r){
//                    pos += (pos - closest).unit * ( 5 - dist)
//                }
//            }
        }
        collisions.withIndex().forEach { (i, value) ->

            Logger.recordOutput("SimulatedArtifact/collision/$i", (
                value.vertices + value.vertices[0]
            ).map { it / 39.37 }.toTypedArray())
        }
        if(collisions.isEmpty()) {
            Logger.recordOutput(
                "SimulatedArtifact/collision",
                arrayOf<Vector3D>()
            )
        }
        log("collisions") value collisions.size

        log("closest") value ( interacts.map {
            it.faces.map {
                it.closestPoint(newpos) - newpos
            }
        }.flatten().minBy { it.mag } + newpos ) / 39.37

        pos += vel * deltaTime

        log("hit") value collisions.isNotEmpty()
        log("pos") value pos / 39.37
        log("vel") value vel / 39.37

    }
}