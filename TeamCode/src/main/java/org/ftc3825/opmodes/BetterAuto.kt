package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals
import kotlin.math.floor
import kotlin.math.PI

@Autonomous(name = "2+0", group = "a")
class BetterAuto: CommandOpMode() {
    override fun init() {
        initialize()
        Globals.AUTO = true


        //Drivetrain.imu.resetYaw()
        //Drivetrain.encoders.forEach { it.resetPosition() }
//        Drivetrain.pinpoint.setPosition(
//            org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
//                DistanceUnit.INCH,
//                8.0, -65.0,
//                AngleUnit.RADIANS,
//                0.0
//            )
//        )

        InstantCommand {
            Arm.pitchUp()
            Claw.pitchUp()
            Claw.grab()
        }.schedule()

        val path1 = Path(
            Line(
                8,-65,
                8,-50
            )
        )
        val path2 = Path(
            Line(
                8,-50,
                62,-36
            ).withHeading( 3 * PI / 2 ),
            Line(
                62,-36,
                62,-38
            )
            
        )
        val path3 = Path(
            Line(
                62,-28,
                8,-50
            ).withHeading( PI / 2 )
        )

        val path4 = Path(
            Line(
                8,-50,
                60,-65
            )
        )

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1/CommandScheduler.deltaTime) }
        Telemetry.addFunction("") { Drivetrain.pos.toString() }
        Telemetry.addFunction("vector") { path1.pose(Drivetrain.pos)}
        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("endHeading") { path3[-1].endHeading }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()

        (
                InstantCommand {
                    Arm.pitchDown()
                    Claw.pitchUp()
                    Claw.grab()
                }
            andThen OuttakeSlides.runToPosition(1300.0)
            andThen FollowPathCommand(path1)
            andThen OuttakeSlides.runToPosition(1450.0)
            andThen InstantCommand { Claw.release() }
            andThen OuttakeSlides.runToPosition(200.0)
            andThen FollowPathCommand(path2)
            andThen InstantCommand { Claw.grab() }
            andThen OuttakeSlides.runToPosition(1200.0)
            andThen FollowPathCommand(path3)
            andThen OuttakeSlides.runToPosition(1000.0)
            andThen InstantCommand { Claw.release() }
            andThen FollowPathCommand(path4)

        ).schedule()
    }
}
