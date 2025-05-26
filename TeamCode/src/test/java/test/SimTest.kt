package test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.RunMotorToPower
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.command.LogCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.sim.SimulatedHardwareMap
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.graph.Graph
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.firstinspires.ftc.teamcode.util.slideMotorName
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.junit.Test
import kotlin.math.abs

class SimTest: TestClass() {
    fun createTestData(){
        val Sub = object: Subsystem<Subsystem.DummySubsystem>() {
            val motor = HWQue.motor("test", Component.Direction.FORWARD)

            override val components: List<Component> = arrayListOf<Component>(motor)

            override fun update(deltaTime: Double) { }

        }
        Sub.reset()
        Sub.motor.hardwareDevice.resetDeviceConfigurationForOpMode()

        val moveCommand = (
                        RunMotorToPower( 1.0, Sub, Sub.motor)
                andThen RunMotorToPower(-1.0, Sub, Sub.motor)
                andThen RunMotorToPower( 0.8, Sub, Sub.motor)
                andThen RunMotorToPower(-0.8, Sub, Sub.motor)
                andThen RunMotorToPower( 0.6, Sub, Sub.motor)
                andThen RunMotorToPower(-0.6, Sub, Sub.motor)
                andThen RunMotorToPower( 0.4, Sub, Sub.motor)
                andThen RunMotorToPower(-0.4, Sub, Sub.motor)
                andThen RunMotorToPower( 0.2, Sub, Sub.motor)
                andThen RunMotorToPower(-0.2, Sub, Sub.motor)
                andThen RunMotorToPower( 0.1, Sub, Sub.motor)
                andThen RunMotorToPower(-0.1, Sub, Sub.motor)

                )
        val logCommand = LogCommand(Sub)

        (logCommand racesWith moveCommand).schedule()

        val graph = Graph(
            Function({ Sub.motor.acceleration }),
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

        val simulated = SimulatedHardwareMap.get(DcMotor::class.java, slideMotorName)

        simulated as SimulatedMotor


        val fake = HWQue.motor(slideMotorName)

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
                "            \"maybe add other data types as well?\" : \"12345\",\n" +
                "        },\n" +
                "    ],\n" +
                "}"

        DataAnalyzer.load("src/test/java/test/testData.json")

        assertEqual(tokenize( DataAnalyzer.data ), tokenize(testData))
    }
}
