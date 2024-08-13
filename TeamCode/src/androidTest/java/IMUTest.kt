package org.ftc3825.androidTest

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import org.ftc3825.component.IMU
import org.ftc3825.component.IMU.Direction
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeIMU
import org.ftc3825.util.TestClass
import org.junit.Assert
import org.junit.Test

class IMUTest: TestClass() {

    @Test
    fun testOrientation(){
        val test = IMU("IMU", hardwareMap)
        test.configureOrientation(logo=Direction.UP, usb=Direction.FORWARD)

        val hardwareDevice = hardwareMap.get(com.qualcomm.robotcore.hardware.IMU::class.java, "IMU") as FakeIMU

        val parameters = hardwareDevice.getParameters().imuOrientationOnRobot
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