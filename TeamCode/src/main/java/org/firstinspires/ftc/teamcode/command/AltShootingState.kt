package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.Globals
import java.util.function.Supplier
import kotlin.math.PI
import kotlin.math.pow

class AltShootingState(val pos: Supplier<Pose2D>): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel)
    override fun execute() {
        Flywheel.usingFeedback = true
        val dist = (
            pos.get().vector - Globals.goalPose.groundPlane
        ).mag

        Hood.targetAngle = PI/2 - (
            if(dist > 20) {(
                -(3.30185*10.0.pow(-8)) * dist.pow(4)
                +0.00000849965 * dist.pow(3)
                -0.000756316 * dist.pow(2)
                +0.021754 * dist
                +1.1

            )} else  1.36
        )
        Flywheel.targetState = VaState(
            Flywheel.getVel(
                PI/2 - Hood.targetAngle,
                dist
            ),
            0.0
        )

    }

    override fun end(interrupted: Boolean) {
        Flywheel.motors.forEach { it.power = 0.0 }
        Flywheel.usingFeedback = false
        Hood.targetAngle = Hood.minAngle
        Hood.update()
    }
}
