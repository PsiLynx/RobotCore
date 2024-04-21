package org.firstinspires.ftc.teamcode.component

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.util.Rotation2D

class IMU(name: String, hardwareMap: HardwareMap, var unit: AngleUnit = RADIANS) {
    private val imu: IMU
    private var offset = 0.0

    init {
        imu = hardwareMap.get(IMU::class.java, name)
    }

    fun setUnit(unit: AngleUnit) {
        if (unit == this.unit) {
            return
        }
        if (unit == DEGREES) {
            throw IllegalArgumentException(
                "Radians are enforced. unit in IMU.setUnit must be AngleUnit.RADIANS"
            )
        }
        this.unit = unit
        if (unit == DEGREES) {
            offset *= 180 / Math.PI
        } else {
            offset /= 180 * Math.PI
        }
    }

    fun configureOrientation(logoDirection: Int, USBDirection: Int) {
        imu.initialize(
                IMU.Parameters(RevHubOrientationOnRobot(
                        logoDirections[logoDirection],
                        USBDirections[USBDirection]
                ))
        )
    }

    val pich: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getPitch(unit))
    val roll: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getRoll(unit))
    var yaw: Rotation2D
        get() = Rotation2D(imu.robotYawPitchRollAngles.getYaw(unit) + offset)
        set(newYaw: Rotation2D):Unit {offset = (newYaw - yaw).toDouble()}

    companion object {
        var DEGREES = AngleUnit.DEGREES
        var RADIANS = AngleUnit.RADIANS
        var logoDirections = arrayOf(
                LogoFacingDirection.UP,
                LogoFacingDirection.DOWN,
                LogoFacingDirection.LEFT,
                LogoFacingDirection.RIGHT,
                LogoFacingDirection.FORWARD,
                LogoFacingDirection.BACKWARD)
        var USBDirections = arrayOf(
                UsbFacingDirection.UP,
                UsbFacingDirection.DOWN,
                UsbFacingDirection.LEFT,
                UsbFacingDirection.RIGHT,
                UsbFacingDirection.FORWARD,
                UsbFacingDirection.BACKWARD)
        var UP = 0
        var DOWN = 1
        var LEFT = 2
        var RIGHT = 3
        var FORWARD = 4
        var BACKWARD = 5
    }
}
