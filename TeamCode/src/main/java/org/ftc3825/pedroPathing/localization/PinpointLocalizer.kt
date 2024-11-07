package org.ftc3825.pedroPathing.localization

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.pedroPathing.util.NanoTimer
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import kotlin.concurrent.timer
import kotlin.math.PI

object PinpointLocalizer: Localizer() {

    private val yOffset = -133.35 // offset of strafe  pod in mm
    private val xOffset = 173.04 // offset of forward pod in mm

    private var _delta = Pose2D()
    private var _position = Pose2D()
    private var startPose = Pose2D()
    private var offsetPose = Pose2D()

    private var rotations = 0
    private var timer = NanoTimer()

    val pinpoint = GlobalHardwareMap.get(
        GoBildaPinpointDriver::class.java, "odo"
    )
    init {
        pinpoint.setOffsets(xOffset, yOffset)
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
        pinpoint.setEncoderDirections(
            GoBildaPinpointDriver.EncoderDirection.REVERSED,
            GoBildaPinpointDriver.EncoderDirection.FORWARD

        )
        pinpoint.resetPosAndIMU()
    }


    override fun getPose(): Pose {
        return Pose(
            _position.x + offsetPose.x + startPose.x,
            _position.y + offsetPose.y + startPose.y,
            _position.heading + offsetPose.heading + startPose.heading,
        )
    }

    override fun getVelocity(): Pose {
        return Pose(
            _delta.x,
            _delta.y,
            _delta.heading
        )
    }

    override fun setStartPose(setStart: Pose?) {
        startPose = Pose2D(
            setStart?.x ?: 0.0,
            setStart?.y ?: 0.0,
            setStart?.heading ?: 0.0
        )
    }

    override fun setPose(setPose: Pose?) {
        startPose = Pose2D(
            setPose?.x ?: 0.0,
            setPose?.y ?: 0.0,
            setPose?.heading ?: 0.0
        )
    }

    override fun update() {
        var deltaTime = timer.elapsedTimeSeconds
        timer.resetTimer()

        pinpoint.update()

        val pose = pinpoint.position!!
        val newPosition = Pose2D(
            pose.getX(DistanceUnit.INCH),
            pose.getY(DistanceUnit.INCH),
            heading = pose.getHeading(AngleUnit.RADIANS)
        ) + Drivetrain.positionOffset

        if(newPosition.heading < 0.5 && _position.heading > 6){
            rotations ++
        }

        if(newPosition.heading > 6 && _position.heading < 0.5){
            rotations --
        }

        _delta = (newPosition - _position).vector / deltaTime + Rotation2D(newPosition.heading - _position.heading)
        _position = newPosition


    }

    override fun getTotalHeading(): Double {
        return rotations * 2 * PI + _position.heading
    }

    override fun getForwardMultiplier(): Double {
        return 0.0
    }

    override fun getLateralMultiplier(): Double {
        return 0.0
    }

    override fun getTurningMultiplier(): Double {
        return 0.0
    }
}