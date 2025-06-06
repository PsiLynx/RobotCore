package test

import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.json.JsonList
import org.firstinspires.ftc.teamcode.util.json.findClosing
import org.firstinspires.ftc.teamcode.util.json.jsonObject
import org.firstinspires.ftc.teamcode.util.json.removeTabs
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.nanoseconds
import org.junit.Test

class JsonTest: TestClass() {
    @Test fun testOutput() {
        var obj = jsonObject {
            "name" `is` "john"
            jsonObject("address") {
                "street" `is` "main st"
                jsonObject("house") {
                    "size" `is` "big"
                }
            }
        }

    }
    @Test fun listTest() {

        val obj = jsonObject {
            "seconds" `is` nanoseconds(Globals.currentTime).toString()
            "voltage" `is` Globals.robotVoltage
            "components" `is` JsonList(Drivetrain.motors.map {
                jsonObject {
                    "name" `is` it.name
                    "voltage" `is` (it.lastWrite or 0.0) * Globals.robotVoltage
                    "position" `is` it.ticks
                }
            })
        }
    }
    @Test fun tokenizeTest(){
        val json = jsonObject {
            "string" `is` "Hello, World!"
            "integer" `is` 42
            "boolean" `is` true
            "double" `is` 3.14
            "list" `is` JsonList(arrayListOf(
                1,
                2f,
                3L,
                "four",
                5.0
            ))
            jsonObject("nestedObject") {
                "nestedString" `is` "Nested Hello"
                "nestedInt" `is` 100
                "nestedBoolean" `is` false
            }
            "nestedArray" `is` JsonList(arrayListOf(
                jsonObject {
                    "arrayString1" `is` "Array Hello 1"
                    "arrayInt1" `is` 1
                },
                jsonObject {
                    "arrayString2" `is` "Array Hello 2"
                    "arrayInt2" `is` 2
                },
                "Array Hello 3"
            ))
            "mixedArray" `is` JsonList(listOf("String", 123, false, 4.56))
            jsonObject("empty object") { }
        }
        val str = json.toString()
        print(str)
        print(tokenize(str))
        assert(tokenize(str).toString() == json.toString())
    }

    @Test fun testFindClosing(){
        val str = "{12345}"

        assertEqual(6, str.findClosing('{'))

        val str2 = "{123{567}9}"
        assertEqual(10, str2.findClosing('{'))
    }
    @Test fun testTabRemove(){
        val str = "    \"this is a test\"    "
        assertEqual(str.removeTabs(), "\"this is a test\"")
    }
}
