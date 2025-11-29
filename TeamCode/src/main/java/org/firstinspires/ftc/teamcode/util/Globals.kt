package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.trajcode.ComputeGoalThings
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance = BLUE //by SelectorInput("alliance", BLUE, RED)
    var randomization = Randomization.UNKNOWN

    //val throughPointOffsetCenter = Vector2D(-2,2)
    val throughPointCenter get() =
        Vector2D(-2,2)


    val throughPointSide get() =
        Vector2D(-2,0.5)


    val throughPoint get() = ComputeGoalThings.verticalThroughPoint

    /**
     * This is the maximum distance from the shooting line
     * that the goapPoint would be considered to be goalPoseCenter
     */

    val robotWidth = 10

    val artifactDiameter = 5

    val goalPoseCenter get() =
             if(alliance == RED ) Vector3D( 68, 68, 41) - Vector3D(artifactDiameter/2,artifactDiameter/2,0)
        else if(alliance == BLUE) Vector3D(-68, 68, 41) - Vector3D(-artifactDiameter/2,artifactDiameter/2,0)
        else Vector3D()

    val goalPose get() = ComputeGoalThings.goalPos(Drivetrain.position.vector)

    //Shooter globals:
    var flywheelOffset = Vector2D(-7, 5)
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
