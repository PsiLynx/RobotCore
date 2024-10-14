package org.ftc3825.component

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.Globals
import org.ftc3825.util.Rotation2D

class IMU(name: String) {
    private val imu = GlobalHardwareMap.get(IMU::class.java, name)
    private var offset = 0.0
    private val unit = RADIANS

    fun configureOrientation(logo: Direction, usb: Direction) {
        imu.initialize(
            IMU.Parameters(RevHubOrientationOnRobot(
                logoDirections[logo],
                USBDirections[usb]
            ))
        )
    }

    val pitch: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getPitch(unit))
    val roll: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getRoll(unit))
    var yaw: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getYaw(unit) + offset)
        set(newYaw) { offset = (newYaw - yaw).toDouble() }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT, FORWARD, BACKWARD
    }

    companion object {
        var RADIANS = AngleUnit.RADIANS
        var logoDirections = mapOf(
            Direction.UP       to LogoFacingDirection.UP,
            Direction.DOWN     to LogoFacingDirection.DOWN,
            Direction.LEFT     to LogoFacingDirection.LEFT,
            Direction.RIGHT    to LogoFacingDirection.RIGHT,
            Direction.FORWARD  to LogoFacingDirection.FORWARD,
            Direction.BACKWARD to LogoFacingDirection.BACKWARD)
        var USBDirections = mapOf(
            Direction.UP       to UsbFacingDirection.UP,
            Direction.DOWN     to UsbFacingDirection.DOWN,
            Direction.LEFT     to UsbFacingDirection.LEFT,
            Direction.RIGHT    to UsbFacingDirection.RIGHT,
            Direction.FORWARD  to UsbFacingDirection.FORWARD,
            Direction.BACKWARD to UsbFacingDirection.BACKWARD)
    }
}
