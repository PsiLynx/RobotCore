package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.JsonList
import org.firstinspires.ftc.teamcode.util.JsonObject
import org.firstinspires.ftc.teamcode.util.jsonObject
import org.firstinspires.ftc.teamcode.util.nanoseconds
import org.junit.Test

class JsonTest {
    @Test
    fun testOutput() {
        var obj = jsonObject {
            "name" `is` "john"
            jsonObject("address") {
                "street" `is` "main st"
                jsonObject("house") {
                    "size" `is` "big"
                }
            }
        }

        println(obj)
    }

    @Test
    fun listTest() {
        var hwmap = FakeHardwareMap()


        var obj = jsonObject {
            "seconds" `is` nanoseconds(System.nanoTime() - 0).toString()
            "voltage" `is` Robot.voltage
            "motors" `is` JsonList<JsonObject>(Drivetrain.Motors.map {
                jsonObject {
                    "name" `is` it.name
                    "voltage" `is` it.lastWrite * Robot.voltage
                    "position" `is` it.positsion
                }
            })
        }

        println(obj)
    }
}