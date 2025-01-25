package test

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Motor
import org.ftc3825.sim.DataAnalyzer
import org.ftc3825.command.LogCommand
import org.ftc3825.component.Component
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.sim.SimulatedMotor
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Slides
import org.ftc3825.util.TestClass
import org.ftc3825.util.centimeters
import org.ftc3825.util.graph.Graph
import org.ftc3825.util.json.tokenize
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.slideMotorName
import org.ftc3825.util.graph.Function
import org.junit.Test
import kotlin.math.abs

class SimTest: TestClass() {
    fun createTestData(){
        Slides.reset()
        Slides.motor.hardwareDevice.resetDeviceConfigurationForOpMode()

        val moveCommand = (
                        RunMotorToPower( 1.0, Slides, Slides.motor)
                andThen RunMotorToPower(-1.0, Slides, Slides.motor)
                andThen RunMotorToPower( 0.8, Slides, Slides.motor)
                andThen RunMotorToPower(-0.8, Slides, Slides.motor)
                andThen RunMotorToPower( 0.6, Slides, Slides.motor)
                andThen RunMotorToPower(-0.6, Slides, Slides.motor)
                andThen RunMotorToPower( 0.4, Slides, Slides.motor)
                andThen RunMotorToPower(-0.4, Slides, Slides.motor)
                andThen RunMotorToPower( 0.2, Slides, Slides.motor)
                andThen RunMotorToPower(-0.2, Slides, Slides.motor)
                andThen RunMotorToPower( 0.1, Slides, Slides.motor)
                andThen RunMotorToPower(-0.1, Slides, Slides.motor)

                )
        val logCommand = LogCommand(Slides)

        (logCommand racesWith moveCommand).schedule()

        val graph = Graph(
            Function({ Slides.motor.acceleration }),
            Function({0.0}, '|'),
            min = -11000.0,
            max = 11000.0
        )

        var i = 0
        while( !moveCommand.isFinished() ){
            CommandScheduler.update()

            graph.printLine()

            if( i > 1E4) throw Error("createTestData is overflowing again")
            i ++

        }
        logCommand.end(interrupted = true)

    }

    fun testSimulatedMotor(){

        val motor = SimulatedHardwareMap.get(DcMotor::class.java, slideMotorName)

        motor as SimulatedMotor


        val simulated = Motor(
            slideMotorName,
            435,
            wheelRadius = centimeters(1),
            controllerParameters = PIDFGParameters(
                P = 0.0003,
                I = 0.000,
                D = 0.001,
                F = 0,
            )
        )
        val fake = Motor(
            slideMotorName,
            435,
            wheelRadius = centimeters(1),
            controllerParameters = PIDFGParameters(
                P = 0.0003,
                I = 0.000,
                D = 0.001,
                F = 0,
            )
        )

        val subsystem = object : Subsystem<Subsystem.DummySubsystem> {
            override val components: ArrayList<Component>
                get() = arrayListOf(simulated, fake)

            init {
                motors.forEach { it.useInternalEncoder() }
            }

            override fun update(deltaTime: Double) { components.forEach {it.update(deltaTime)} }

        }
        subsystem.reset()

        simulated.runToPosition(1000)
        fake.runToPosition(1000)

            (
                subsystem.justUpdate()
                    until {
                        abs(simulated.ticks - 1000) < 15
                    }
            ).schedule()

        val graph = Graph(
            Function({simulated.ticks}, 'S'),
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
            simulated.ticks - 1000,
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

        DataAnalyzer.load("src/test/java/test/testData.json")

        assertEqual(tokenize( DataAnalyzer.data ), tokenize(testData))
    }
}
