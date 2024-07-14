package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.SimulatedMotor
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.firstinspires.ftc.teamcode.util.assertGreater
import org.firstinspires.ftc.teamcode.util.flMotorName
import org.firstinspires.ftc.teamcode.util.isGreaterThan
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.junit.Test

class SimTest: TestClass() {
    @Test fun testAnalyzerLoadsData(){
        DataAnalyzer.loadTestData()
        val motors = DataAnalyzer.analyze()

        val simulated = SimulatedMotor(motors[flMotorName]!!)

        assert(
            simulated.maxVelocityInTicksPerSecond
            isGreaterThan 0
        )
        assert(
            FakeMotor().maxVelocityInTicksPerSecond
            isGreaterThan simulated.maxVelocityInTicksPerSecond
        )
    }

    @Test fun testSimultatedMotor(){
        DataAnalyzer.loadTestData()
        DataAnalyzer.analyze()

        val motor = SimulatedMotor(DataAnalyzer.motors[flMotorName]!!)


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