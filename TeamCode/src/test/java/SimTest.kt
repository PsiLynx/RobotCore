package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Motor
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.sim.DataAnalyzer
import org.ftc3825.sim.LogCommand
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.sim.SimulatedMotor
import org.ftc3825.subsystem.Slides
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.ftc3825.util.assertGreater
import org.ftc3825.util.assertWithin
import org.ftc3825.util.centimeters
import org.ftc3825.util.graph.Graph
import org.ftc3825.util.isWithin
import org.ftc3825.util.json.tokenize
import org.ftc3825.util.of
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.slideMotorName
import org.ftc3825.util.graph.Function
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
            Function({Slides.position}),
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