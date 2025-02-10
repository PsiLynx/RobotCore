package org.ftc3825.opmodes

import org.ftc3825.command.hang
import org.ftc3825.command.intakeClipsEnd
import org.ftc3825.command.intakeClipsStart
import org.ftc3825.command.intakeSample
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.RepeatCommand
import org.ftc3825.command.transfer
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

class Auto: CommandOpMode() {
    override fun init() {
        initialize()

        arrayListOf(
            Extendo, Telemetry, Drivetrain,
            OuttakeArm, OuttakeClaw, SampleIntake
        ).forEach { it.reset() }

        val robotStart = Pose2D(9, -66, PI / 2)
        Drivetrain.position = robotStart

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollDown(),
                OuttakeClaw.outtakePitch()
            ) andThen followPath {
                start(robotStart.vector)
                lineTo(9, -30, HeadingType.constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle()
            andThen ( OuttakeArm.setPowerCommand(-0.5) withTimeout(0.5) )
            andThen OuttakeClaw.release()

        )
        val goToHumanPlayer = (
            Command.parallel(
                followPath {
                    start(9, -30)
                    lineTo(intakeClipsStart, HeadingType.constant(PI / 2))
                },
                OuttakeArm.wallAngle(),
                OuttakeClaw.release(),
                OuttakeClaw.rollDown(),
            )
            andThen OuttakeClaw.grab()
            withName "go to human player"
        )
        (
            hangPreload
            andThen goToHumanPlayer
            andThen followPath {
                start(intakeClipsEnd)
                lineTo(0, -30, HeadingType.constant(PI / 2))
            }
            andThen (intakeSample parallelTo hang)
            andThen RepeatCommand(
                transfer andThen (intakeSample parallelTo hang),
                8
            )
        ).schedule()


        Telemetry.justUpdate().schedule()
    }
}