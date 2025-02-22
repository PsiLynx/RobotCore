package org.ftc3825.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.ftc3825.gvf.Path
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Vector2D
import kotlin.math.cos
import kotlin.math.sin

object Drawing {
    private const val ROBOT_RADIUS: Double = 9.0
    private lateinit var packet: TelemetryPacket
    private val canvas: Canvas
        get() = packet.fieldOverlay()

    init { ensurePacketExists() }

    private fun ensurePacketExists() {
        packet = TelemetryPacket(false)

        canvas.setTranslation(0.0, 0.0)
        canvas.setRotation(-Math.PI / 2)
        packet.field().drawImage(
            "/dash/into-the-deep.png",
            0.0, 0.0,
            144.0, 144.0,
            Math.PI,
            72.0, 72.0,
            false
        )
    }

    fun drawRobot(pose: Pose2D, color: String) {
        canvas.setStroke(color)
        canvas.setStrokeWidth(1)
        canvas.strokeCircle(pose.x, pose.y, ROBOT_RADIUS)
        val v = pose.heading * Vector2D(0.0, 1.0) * ROBOT_RADIUS
        canvas.strokeLine(
            pose.x,
            pose.y,
            pose.x + v.x,
            pose.y + v.y
        )
    }

    fun sendPacket() {
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        ensurePacketExists()
    }

    fun drawLine(x: Double, y: Double, theta: Double, color: String) {
        canvas.setStroke(color)
        canvas.strokeLine(
            x, y,
            cos(theta) * ROBOT_RADIUS + x,
            sin(theta) * ROBOT_RADIUS + y
        )
    }

    fun drawPoint(x: Double, y: Double, color: String) {
        canvas.setStroke(color)
        canvas.fillCircle(x, y, 2.0)
    }

    fun drawGVFPath(path: Path, active: Boolean) {
        for (i in 0 until path.numSegments) {
            val points = Array(2) { DoubleArray(100) }
            for (t in 0..99) {
                val point = path[i].point(t / 100.0)
                points[0][t] = point.x
                points[1][t] = point.y
            }
            var color = "green"
            if (active) {
                color = "orange"
                if (path.currentPath == path[i]) {
                    color = "red"
                }
            }
            canvas.setStroke(color)
            canvas.strokePolyline(points[0], points[1])
        }
    }

    fun drawPoseHistory(poseHistory: Array<Pose2D>, color: String) {
        val points = Array(2) { DoubleArray(poseHistory.size) }

        for (i in poseHistory.indices) {
            points[0][i] = poseHistory[i].x
            points[1][i] = poseHistory[i].y
        }

        canvas.setStroke(color)
        canvas.strokePolyline(points[0], points[1])
    }
}
