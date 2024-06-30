package org.firstinspires.ftc.teamcode.androidTest

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import org.firstinspires.ftc.teamcode.component.IMU
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeIMU
import org.junit.Assert
import org.junit.Test

class IMUTest {
    val hardwaremap = FakeHardwareMap(null, null)

    @Test
    fun testOrientation(){
        var test = IMU("IMU", hardwaremap)
        test.configureOrientation(logo= IMU.UP, usb= IMU.FORWARD)

        var hardwareDevice = hardwaremap.get(com.qualcomm.robotcore.hardware.IMU::class.java, "IMU") as FakeIMU

        var parameters = hardwareDevice.getParameters().imuOrientationOnRobot
        Assert.assertTrue(
            parameters.equals(
                com.qualcomm.robotcore.hardware.IMU.Parameters(
                    RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                    )
                )
            )
        )

    }
}