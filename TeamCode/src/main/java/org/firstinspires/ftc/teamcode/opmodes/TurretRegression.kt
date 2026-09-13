package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.For
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.While
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI

@TeleOp(group = "a")
class TurretRegression: CommandOpMode() {
    val dSpeed = PI/16
    var speed = dSpeed
    val min = PI/2
    val max = 3*PI/2
    var position = min
    var prevTime = Globals.currentTime
    override fun postSelector() {
        val command = (
            //while speed is less then PI
            While({speed < PI}, (
                //goto left
                InstantCommand{ Turret.targetState = PvState(Rotation2D(min), Rotation2D(0.0))}
                        andThen
                WaitCommand(1)

                andThen InstantCommand{
                    position = min
                    prevTime = Globals.currentTime
                }
                        //itterate throug the arc
                andThen While({position < max}, InstantCommand{
                        position += speed*(Globals.currentTime-prevTime)
                        prevTime = Globals.currentTime
                        Turret.targetState = PvState(Rotation2D(position), Rotation2D(0))
                })
                andThen InstantCommand{
                    speed += dSpeed
                }
                )
            )
        )

        command.schedule()
    }
}