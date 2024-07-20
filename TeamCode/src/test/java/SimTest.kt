package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.RunMotorToPower
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap

import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.LogCommand
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.subsystem.Slides
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.firstinspires.ftc.teamcode.util.assertGreater
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.centimeters
import org.firstinspires.ftc.teamcode.util.flMotorName
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.firstinspires.ftc.teamcode.util.isGreaterThan
import org.firstinspires.ftc.teamcode.util.isWithin
import org.firstinspires.ftc.teamcode.util.of

import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.pid.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.slideMotorName
import org.junit.Test

class SimTest: TestClass() {
    @Test fun createTestData(){
        Slides.init(FakeHardwareMap)
        Slides.motor.motor.resetDeviceConfigurationForOpMode()
        val moveCommand = (
                        RunMotorToPower( 1.0, Slides, Slides.motor)
//                andThen RunMotorToPower(-1.0, Slides, Slides.motor)
//                andThen RunMotorToPower( 0.8, Slides, Slides.motor)
//                andThen RunMotorToPower(-0.8, Slides, Slides.motor)
//                andThen RunMotorToPower( 0.6, Slides, Slides.motor)
//                andThen RunMotorToPower(-0.6, Slides, Slides.motor)
//                andThen RunMotorToPower( 0.4, Slides, Slides.motor)
//                andThen RunMotorToPower(-0.4, Slides, Slides.motor)
//                andThen RunMotorToPower( 0.2, Slides, Slides.motor)
//                andThen RunMotorToPower(-0.2, Slides, Slides.motor)
//                andThen RunMotorToPower( 0.1, Slides, Slides.motor)
//                andThen RunMotorToPower(-0.1, Slides, Slides.motor)

                )
        val logCommand = LogCommand(Slides)

        CommandScheduler.schedule(moveCommand)
        CommandScheduler.schedule(logCommand)

        var graph = Graph(
            Function({Slgit add T   gitides.position}),
            Function({0.0}, '|'),
            Function({Slides.motor.lastWrite * 1000}, '+'),
            min = -200.0,
            max = 200.0
        )

        var i = 0;
        while( !moveCommand.isFinished() ){
            CommandScheduler.update()

            graph.printLine()

            if( i > 1E4) throw Error("createTestData is overflowing again")
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

        var wrapped = Motor(
            slideMotorName,
            SimulatedHardwareMap,
            435,
            wheelRadius = centimeters(1),
            controllerParameters = PIDFGParameters(
                P = 0.0003,
                I = 0.000,
                D = 0.001,
                F = 0,
                G = 0
            )
        )

        wrapped.runToPosition(1000)

        var subsystem = object : Subsystem{
            override var initialized = false

            override val motors: ArrayList<Motor>
                get() = arrayListOf(wrapped)

            override fun init(hardwareMap: HardwareMap) {
                wrapped.useInternalEncoder()
                wrapped.motor.resetDeviceConfigurationForOpMode()
            }

            override fun update(deltaTime: Double) { wrapped.update(deltaTime) }

        }

        CommandScheduler.schedule(
            subsystem.run { } until { wrapped.position isWithin 15 of 1000 }
        )

        var graph = Graph(
            Function({wrapped.position}, '*'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 5000.0
        )

        for( i in 0..1000){
            CommandScheduler.update()
            graph.printLine()
        }

        assertWithin(
            wrapped.position - 1000,
            40
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