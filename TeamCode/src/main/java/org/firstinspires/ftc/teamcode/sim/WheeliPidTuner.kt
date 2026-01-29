package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.firstinspires.ftc.teamcode.util.log
import java.lang.Thread.sleep

class WheeliPidTuner {

    val Fg = Vector2D(0, -386)
    var theta = Rotation2D(PI/8)
    val radius = 1.0
    val dt = 0.1
    val m = 1.0
    var time = 0.0
    var position: Vector2D = {Vector2D(
        sin(theta.toDouble())*radius,
        cos(theta.toDouble())*radius)}()
        set(value){
            theta = (field - value).theta
            field = value
        }
    var acc = Vector2D(0, 0)

    var velocity = Vector2D(0, 0)



    fun main(){
        Logger.reset()
        Logger.setTimeSource { time }
        Logger.addDataReceiver(RLOGServer())
        Logger.start()
        sleep(50)

        while(time < 30.0){
            Logger.periodicBeforeUser()

            var Fn = Fg * sin(theta.toDouble())

            acc = Fn / m
            velocity += acc * dt
            position += velocity * dt

            log("position") value position
            log("velocity") value velocity
            log("acc") value acc
            log("theta") value theta
            sleep(50)

            time += dt
            Logger.periodicAfterUser(0.0, 0.0)
        }
    }
}
