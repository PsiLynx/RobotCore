package test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager.qued
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.centimeters
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.junit.Test
import kotlin.math.abs

class SimTest: TestClass() {

    fun testSimulatedMotor(){
        val slideMotorName = "hardwareDevice"

        val simulated = SimulatedHardwareMap.get(DcMotor::class.java, slideMotorName)

        simulated as SimulatedMotor


        val fake = Motor(
            FakeMotor(),
            slideMotorName,
            0,
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ).qued()

        val controller = PIDFController(
            P = 0.0003,
            D = 0.001,
            pos = { fake.position },
            apply = {
                fake.power = it
                simulated.power = it
            }
        )

        val subsystem = object : Subsystem<Subsystem.DummySubsystem>() {
            override val components: List<Component>
                get() = arrayListOf(fake)

            init {
                motors.forEach { it.useInternalEncoder(1.0, 1.0) }
            }

            override fun update(deltaTime: Double) {
                components.forEach {it.update(deltaTime)}
                controller.updateController(deltaTime)
            }

        }
        subsystem.reset()


            (
                subsystem.justUpdate()
                    until {
                        abs(simulated.currentPosition - 1000) < 15
                    }
            ).schedule()

        val graph = Graph(
            Function({simulated.currentPosition.toDouble()}, 'S'),
            Function({fake.ticks}, 'F'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 2000.0
        )

        for( i in 0..200){
            CommandScheduler.update()
            if(i % 20 == 0){
                graph.printLine()
            }

        }

        assertWithin(
            simulated.currentPosition - 1000,
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
                "            \"maybe add other data types as well?\" : " +
                "\"12345\",\n" +
                "        },\n" +
                "    ],\n" +
                "}"

        DataAnalyzer.load("src/test/java/test/testData.json")

        assertEqual(tokenize( DataAnalyzer.data ), tokenize(testData))
    }
}
