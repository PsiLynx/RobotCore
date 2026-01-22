package org.firstinspires.ftc.teamcode.util


import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.psilynx.psikit.core.Logger
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.RED
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI


object Globals {
    var robotVoltage = 13.0

    var running = true

    var alliance by SelectorInput("alliance", BLUE, RED)
    var randomization = Randomization.UNKNOWN

    val throughPoint get() = Vector2D(-2,1)

    val artifactDiameter = 5

    val goalPoseCenter get() =
             if(alliance == RED ) Vector3D( 64, 64, 41) - Vector3D(artifactDiameter/2,artifactDiameter/2,0)
        else if(alliance == BLUE) Vector3D(-64, 64, 41) - Vector3D(-artifactDiameter/2,artifactDiameter/2,0)
        else Vector3D()

    val goalPose get() = goalPoseCenter

    //camera globals:
    /**
    * The offset from the center of the ground plane of
    * the robot to the center of the turret horizontal
    * with the camera.
    **/
    var CameraOffsetA = Vector2D(-5,10)

    /**
     * The horizontal offset from the center of the turret to
     * the center of the camera.
     */
    var CameraOffsetB = 5

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
