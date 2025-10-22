package org.firstinspires.ftc.teamcode.sim

import android.R.attr.top
import org.firstinspires.ftc.teamcode.geometry.Prism3D
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
        pos += vel * deltaTime

        val collisions = interacts.map { it.intersectingFaces(this) }.flatten()
        val hit = collisions.firstOrNull()
        if(hit != null){
            vel -= hit.normal * (vel dot hit.normal) * ( 1 + e )
            Logger.recordOutput("SimulatedArtifact/collision", (
                collisions[0].vertices + collisions[0].vertices[0]
            ).map { it / 39.37 }.toTypedArray())

            log("dist") value (hit.closestPoint(pos) - pos).mag
        }
        else Logger.recordOutput("SimulatedArtifact/collision", arrayOf<Vector3D>())
        log("collisions") value collisions.size

        log("closest") value interacts[0].faces.minBy {
            it.closestPoint(pos).mag
        }.closestPoint(pos) / 39.37
        log("hit") value (hit != null)
        log("pos") value pos / 39.37
        log("vel") value vel / 39.37

    }
}