package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.JsonObject
import org.firstinspires.ftc.teamcode.util.json.eat
import org.firstinspires.ftc.teamcode.util.json.findClosing
import org.firstinspires.ftc.teamcode.util.json.jsonObject
import org.firstinspires.ftc.teamcode.util.json.removeTabs
import org.firstinspires.ftc.teamcode.util.json.tokenize
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

    @Test
    fun tokenizeTest(){
        val json = jsonObject {
            "name" `is` "john"
            jsonObject("house") {
                "size" `is` "big"
                "location" `is` "main st"
                "rooms" `is` JsonList(arrayListOf(
                    "bedroom",
                    "bathroom",
                    "kitchen",
                    "family room",
                    jsonObject {
                        "cans" `is` "true"
                        "granola bars" `is` JsonList(arrayListOf(
                            "belvita",
                            "fig bar",
                            "nature's valley"
                        ))
                    }
                ))
            }
        }
        println(json)
        val str = json.toString()

        assertEqual(tokenize(str), json)
    }

    @Test
    fun testFindClosing(){
        val str = "{12345}"

        assertEqual(6, str.findClosing('{'))

        val str2 = "{123{567}9}"
        assertEqual(10, str2.findClosing('{'))
    }

    @Test
    fun testEat(){
        var str = "01234"
        str = str.eat(2)
        assertEqual(str, "234")
    }

    @Test
    fun testTabRemove(){
        var str = "    \"this is a test\"    "
        assertEqual(str.removeTabs(), "\"this is a test\"")
    }
}