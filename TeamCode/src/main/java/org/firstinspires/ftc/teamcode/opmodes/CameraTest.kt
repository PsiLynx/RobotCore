package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.cv.GamePiecePipeLine
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.openftc.easyopencv.OpenCvCameraRotation


@TeleOp(name = "OpenCvCamera test")
class CameraTest : CommandOpMode() {
    override fun initialize() {
        //Extendo.reset()
        Telemetry.reset()

        val resolution = Vector2D(640, 480)
        val pipeLine = GamePiecePipeLine()
//        val camera = HardwareMap.camera(
//            resolution,
//            pipeLine,
//            OpenCvCameraRotation.SIDEWAYS_LEFT
//        )

        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
