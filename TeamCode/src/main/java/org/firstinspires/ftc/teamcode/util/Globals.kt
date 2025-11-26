package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI
import kotlin.math.cos


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance      = Alliance     .UNKNOWN
    var randomization = Randomization.UNKNOWN

    /**
     * This is the maximum distance from the shooting line
     * that the goapPoint would be considered to be goalPoseCenter
     */
    val centerGoalRange = 12

    val goalPoseCenter get() =
             if(alliance == RED ) Vector3D( 68, 68, 41)
        else if(alliance == BLUE) Vector3D(-68, 68, 41)
        else Vector3D()
    val goalPosSide get() =
        goalPoseCenter - Vector3D(0,-10,0)

    val goalPosBack get() =
        if(alliance == RED ) goalPoseCenter - Vector3D(-10,0,0)
        else if(alliance == BLUE) goalPoseCenter - Vector3D(10,0,0)
        else Vector3D()

    val goalPose: Vector3D get() {
        /**
         * This is the mathematical representation of the shooting lines on the field.
         * @param x The x position of the robot.
         */
        fun shootLine(x: Double): Double {
            if(alliance == RED){
                return x
            }
            if(alliance == BLUE){
                return -x
            }
            return 0.0
        }
        println(Drivetrain.position.x)
        println(shootLine(Drivetrain.position.x))
        //If below the shooting line:
        return if (shootLine(Drivetrain.position.x) > Drivetrain.position.y + cos(PI/4)*centerGoalRange)
            goalPosBack
        //if above the shooting Line:
        else if (shootLine(Drivetrain.position.x) < Drivetrain.position.y - cos(PI/4)*centerGoalRange)
            goalPosSide
        else goalPoseCenter
    }

    //Shooter globals:
    var flywheelOffset = Vector2D(-7,10.5)
    var flywheelRadius = 2.0
    var ballOffset = Vector2D(-flywheelRadius-2.5,0) rotatedBy PI /4


    var isSimulation = false
    var unitTesting = false
    var logReplay = false

    val currentTime: Double
        get() = Logger.getTimestamp()

    enum class Alliance {
        RED, BLUE, UNKNOWN
    }
    enum class Randomization {
        GPP, PGP, PPG, UNKNOWN
    }
    enum class BallColor {
        GREEN, PURPLE, UNKNOWN
    }

}
