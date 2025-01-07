package org.ftc3825.opmodes

import org.ftc3825.command.clip
import org.ftc3825.command.hang
import org.ftc3825.command.intakeClips
import org.ftc3825.command.intakeClipsEnd
import org.ftc3825.command.intakeClipsStart
import org.ftc3825.command.intakeSample
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.RepeatCommand
import org.ftc3825.command.transfer
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.subsystem.ClipIntake
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.subsystem.OuttakeClaw
import org.ftc3825.subsystem.SampleIntake
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D
import kotlin.math.PI

class Auto: CommandOpMode() {
    override fun init() {
        initialize()

        arrayListOf(
            Extendo, Telemetry, ClipIntake, Drivetrain,
            OuttakeArm, OuttakeClaw, SampleIntake
        ).forEach { it.reset() }

        val robotStart = Pose2D(9, 66, PI / 2)
        Drivetrain.position = robotStart

        val hangPreload = (
            Command.parallel(
                OuttakeClaw.grab(),
                OuttakeClaw.rollCenter(),
                OuttakeClaw.outtakePitch()
            ) andThen followPath {
                start(robotStart.vector)
                lineTo(9, -30, HeadingType.Constant(PI / 2))
            } parallelTo OuttakeArm.outtakeAngle()
            andThen ( OuttakeArm.setPower(-0.5) withTimeout(0.5) )
            andThen OuttakeClaw.release()

        )
        val goToHumanPlayer = (
            Command.parallel(
                followPath {
                    start(9, -30)
                    lineTo(intakeClipsStart, HeadingType.Constant(PI / 2))
                },
                OuttakeArm.wallAngle(),
                OuttakeClaw.release(),
                OuttakeClaw.rollCenter(),
            )
            andThen intakeClips
            andThen OuttakeClaw.grab()
            withName "go to human player"
        )
        (
            hangPreload
            andThen goToHumanPlayer
            andThen followPath {
                start(intakeClipsEnd)
                lineTo(0, -30, HeadingType.Constant(PI / 2))
            }
            andThen (intakeSample parallelTo hang)
            andThen RepeatCommand(
                clip andThen transfer andThen (intakeSample parallelTo hang),
                8
            )
        ).schedule()


        Telemetry.telemetry = telemetry
        Telemetry.justUpdate().schedule()
    }
}