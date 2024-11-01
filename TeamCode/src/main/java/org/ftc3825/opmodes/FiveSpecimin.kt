package org.ftc3825.opmodes

import org.ftc3825.command.internal.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Arm
import org.ftc3825.subsystem.Claw
import org.ftc3825.subsystem.Telemetry
import kotlin.math.floor
import kotlin.math.PI

@Autonomous(name = "5+0", group = "a")
class FiveSpecimin: CommandOpMode() {
    override fun init() {
        initialize()

        Drivetrain.imu.resetYaw()
        //Drivetrain.encoders.forEach { it.reset() }
//        Drivetrain.pinpoint.setPosition(
//            org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
//                DistanceUnit.INCH,
//                8.0, -65.0,
//                AngleUnit.RADIANS,
//                0.0
//            )
//        )

        (
            Arm.pitchUp()
            andThen Claw.pitchUp()
            andThen Claw.grab()
        ).schedule()

        var path1 = Path(
            Line(
                8,-65,
                8,-50
            )
        )
        var path2 = Path(
            Line(
                8,-50,
                62,-36
            ).withHeading( 3 * PI / 2 ),
            Line(
                62,-36,
                62,-38
            )
            
        )
        var path3 = Path(
            Line(
                62,-28,
                8,-50
            ).withHeading( PI / 2 )
        )

        var path4 = Path(
            Line(
                8,-50,
                60,-65
            )
        )

        var placePreload = (
            (
                Arm.pitchDown()
                andThen Claw.pitchUp()
                andThen Claw.grab()
            )
            andThen OuttakeSlides.runToPosition(1300.0)
            andThen FollowPathCommand(path1)
            andThen OuttakeSlides.runToPosition(1450.0)
            andThen Claw.release()
        )

        var moveFieldSpecimins = (
                FollowPathCommand(
                    Path(
                        Line(
                            8, -50,
                            35, -50
                        ).withHeading(PI / 2),

                        Line(
                            35, -50,
                            35, -16
                        ).withHeading(PI / 2),
                        Line(
                            35, -16,
                            50, -16
                        ).withHeading(PI / 2),
                        Line(
                            50, -16,
                            50, -55
                        ).withHeading(PI / 2),

                        Line(
                            50, -55,
                            50, -16
                        ).withHeading(PI / 2),
                        Line(
                            50, -16,
                            60, -16
                        ).withHeading(PI / 2),
                        Line(
                            60, -16,
                            60, -55
                        ).withHeading(PI / 2),

                        Line(
                            60, -55,
                            60, -16
                        ).withHeading(PI / 2),
                        Line(
                            60, -16,
                            66, -16
                        ).withHeading(PI / 2),
                        Line(
                            66, -16,
                            66, -55
                        ).withHeading(PI / 2),
                    )
                )
        )

        var moveToCycle = FollowPathCommand(
            Path(
                Line(
                    66, -55,
                    58, -38
                ),
                Line(
                    58, -38,
                    62, -38
                )

            )
        )
        var cycle = (
            Claw.pitchUp()
            parallelTo Arm.pitchDown()
            parallelTo OuttakeSlides.runToPosition(200.0)
            andThen Claw.grab()
            andThen WaitCommand(0.5)
            andThen (
                FollowPathCommand(
                    Path(
                        Line(
                            62, -38,
                            8, -50
                        ).withHeading(PI / 2)
                    )
                )
            )
        )

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("hertz") { floor(1/CommandScheduler.deltaTime) }
        Telemetry.addFunction("") { Drivetrain.position.toString() }
        Telemetry.addFunction("vector") { path1.pose(Drivetrain.position)}
        Telemetry.addFunction("slides") { OuttakeSlides.position }
        Telemetry.addFunction("endHeading") { path3[-1].endHeading }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()

        (
            placePreload
            andThen OuttakeSlides.runToPosition(200.0)
            andThen FollowPathCommand(path2)
            andThen Claw.grab()
            andThen OuttakeSlides.runToPosition(1200.0)
            andThen FollowPathCommand(path3)
            andThen OuttakeSlides.runToPosition(1000.0)
            andThen Claw.release()
            andThen FollowPathCommand(path4)

        ).schedule()
    }
}
