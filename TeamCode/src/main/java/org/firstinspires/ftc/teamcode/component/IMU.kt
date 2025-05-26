package org.firstinspires.ftc.teamcode.component

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D

class IMU(name: String) {
    private val imu = HardwareMap.get(IMU::class.java, name)
    private val unit = RADIANS

    private var offset = Rotation2D()
    var yaw = Rotation2D()
    var lastYaw = Rotation2D()

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
    val delta: Rotation2D
        get() = (yaw - lastYaw).wrap()

    fun update(){
        lastYaw = yaw
        yaw = Rotation2D(imu.robotYawPitchRollAngles.getYaw(unit)) + offset
    }
    fun resetYaw(){
        offset = -Rotation2D(imu.robotYawPitchRollAngles.getYaw(unit))
    }

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
