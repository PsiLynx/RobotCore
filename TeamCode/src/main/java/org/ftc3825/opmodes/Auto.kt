package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.GVF.Spline
import org.ftc3825.command.DriveCommand
import org.ftc3825.command.DriveCommand.Direction.FORWARD
import org.ftc3825.command.DriveCommand.Direction.BACK
import org.ftc3825.command.DriveCommand.Direction.RIGHT
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Intake
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.util.Pose2D
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import org.ftc3825.util.Slides

const val width = 12.0
const val height = 14.0

@Autonomous(name = "Auto", group = "a")
class Auto: CommandOpMode() {
    override fun init() {
        initialize()
        Telemetry.telemetry = telemetry!!
        Globals.AUTO = true

        //Telemetry.addFunction("") { Drivetrain.encoders[0].distance.toString() }
        Telemetry.justUpdate().schedule()

//        InstantCommand {
//            Arm.pitchUp()
//            Claw.pitchUp()
//            Claw.grab ()
//        }.schedule()
//
//        var moveSlidesALittle = OuttakeSlides.runToPosition(580.0)
//
////        var driveForward = (
////            Drivetrain.run {
////                it.setWeightedDrivePower(Pose2D(-0.25, 0, 0))
////            } until { Drivetrain.encoders[0].distance > 10000 } withEnd { Drivetrain.setWeightedDrivePower(Pose2D())  }
////                withTimeout (3)
////        )
//
//        var moveArmUp = (
//            RunCommand(OuttakeSlides) { OuttakeSlides.setPower(0.6) } until { OuttakeSlides.position > 950} withEnd { OuttakeSlides.setPower(0.0)}
//            andThen WaitCommand(1)
//        )
//
//        var retract = InstantCommand {
//            Claw.release()
//        }
//
//        var park = (
//            TimedCommand(0.5) { Drivetrain.setWeightedDrivePower(0.25, 0.0, 0.0) }
//            andThen TimedCommand(6) {
//                Drivetrain.setWeightedDrivePower(Pose2D(0.0, -0.25, -0.02))
//            }
//            andThen (
//                    TimedCommand(2) {
//                Drivetrain.setWeightedDrivePower(Pose2D(0.25, 0.0, 0.0))
//            } withEnd { Drivetrain.setWeightedDrivePower(Pose2D()) }
//                    )
//        )
//
//        ( moveSlidesALittle andThen /*driveForward andThen*/ moveArmUp andThen retract andThen park
//            ).schedule()

        Drivetrain.run {
            it.setWeightedDrivePower(
                0.3, 0.0, 0.0
            )
        }.schedule()
    }
}
