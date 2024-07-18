package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap

import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.LogCommand
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.subsystem.Slides
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.firstinspires.ftc.teamcode.util.assertGreater
import org.firstinspires.ftc.teamcode.util.flMotorName
import org.firstinspires.ftc.teamcode.util.isGreaterThan
import org.firstinspires.ftc.teamcode.util.isWithin
import org.firstinspires.ftc.teamcode.util.of

import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.slideMotorName
import org.junit.Test

class SimTest: TestClass() {
    @Test fun createTestData(){
        Slides.init(hardwareMap)
        val moveCommand = (
                Slides.run { Slides.runToPosition(1000) }
                until {
                    Slides.position isWithin 15 of 1000
                    && Slides.velocity isWithin  5 of 0
                }

                andThen (
                        Slides.run { Slides.runToPosition(-1000) }
                        until {
                            Slides.position isWithin 15 of -1000
                            && Slides.velocity isWithin  5 of 0
                        }
                )
        )
        val logCommand = LogCommand(Slides)

        CommandScheduler.schedule(moveCommand)
        CommandScheduler.schedule(logCommand)

        println(moveCommand.requirements)

        var i = 0;
        while( !moveCommand.isFinished() ){
            CommandScheduler.update()

            println("$i, ${Slides.position}")

            i ++
        }
        logCommand.end(interrupted = true)
    }

    @Test fun testSimultatedMotor(){

        val motor = SimulatedHardwareMap.get(DcMotor::class.java, slideMotorName)

        motor as SimulatedMotor

        assert( motor.data.isNotEmpty() )

        assertGreater(
            motor.maxVelocityInTicksPerSecond,
            0
        )
        assertGreater(
            FakeMotor().maxVelocityInTicksPerSecond,
            motor.maxVelocityInTicksPerSecond
        )

    }

    @Test fun testFileLoader(){
        val testData = "{\n" +
                "    \"what is this for\" : \"testing the file loader\",\n" +
                "    \"why\" : \"Avery has this thing where he unit tests random things that don't need testing\",\n" +
                "    \"wait what?\" : [\n" +
                "        \"yeah\",\n" +
                "        \"he tests stuff that obviously works\",\n" +
                "        \"but then doesnt write tests for other things\",\n" +
                "        \"and those are the things that break\",\n" +
                "        {\n" +
                "            \"broken\" : \"litterally every string extension function\",\n" +
                "            \"mood\" : \":(\",\n" +
                "            \"maybe add other data types as well?\" : \"12345\",\n" +
                "        },\n" +
                "    ],\n" +
                "}"

        DataAnalyzer.load("src/test/java/testData.json")

        assertEqual(tokenize( DataAnalyzer.data ), tokenize(testData))
    }
}