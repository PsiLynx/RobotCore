package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.FtcDashboard
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.GPP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PGP
import org.firstinspires.ftc.teamcode.util.Globals.Randomization.PPG
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log

object Cameras: Subsystem<Cameras>() {
    val obeliskCamera = HardwareMap.obeliskCamera(
        Vector2D(640, 480),
        Vector3D(2, 5, 8),
        YawPitchRollAngles(
            AngleUnit.DEGREES,
            0.0, -60.0, 0.0, 0L
        )

    )
    override val components = listOf<Component>()

    var pose = Pose2D()
    var updateTime = 0.0
    init {
        if(Globals.running) {
            obeliskCamera.build()
        }
    }

    override fun update(deltaTime: Double) {
        obeliskCamera.detections?.forEach {
            println("metadata: " + it?.metadata)
            when(it?.id ?: 0){
                21 -> Globals.randomization = GPP
                22 -> Globals.randomization = PGP
                23 -> Globals.randomization = PPG
                20 -> {
                    val position = it?.robotPose
                    if(position != null) {
                        pose = Pose2D(
                            position.position.y,
                            -position.position.x,
                            position.orientation.getYaw(AngleUnit.RADIANS)
                        )
                        updateTime = Globals.currentTime
                    }
                }
                24 -> {
                    val position = it?.robotPose
                    if(position != null) {
                        pose = Pose2D(
                            position.position.y,
                            -position.position.x,
                            position.orientation.getYaw(AngleUnit.RADIANS)
                        )
                        updateTime = Globals.currentTime
                    }
                }
            }
        }
        log("detections") value (
                obeliskCamera.detections?.map {
                    if(it != null){
                        return@map (
                            it.id.toString()
                            + ", "
                            + it.robotPose.toString()
                            + ", "
                            + it.metadata.toString()
                        )
                    }
                    return@map "none"
                }?.toTypedArray() ?: arrayOf<String>(
                    "no detections",
                    "no detections"
                )
        )
        log("pose") value pose
        log("updateTime") value updateTime
        log("time since last seen") value (Globals.currentTime - updateTime)
    }


}