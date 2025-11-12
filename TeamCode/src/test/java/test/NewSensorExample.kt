package test

import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.Globals

class NewSensorExample {
    fun test(){
        val topDistanceSensor = HardwareMap.topSensor(
            minDist = 0.0,
            maxDist = 100.0,
            zeroVoltage = 0.0,
            maxVoltage = 3.2
        )

        topDistanceSensor.distance // returns the current distance


        val colorSensor = HardwareMap.colorSensor(
            trueValue = Globals.BallColor.GREEN,
            falseValue = Globals.BallColor.PURPLE
        )

        colorSensor.value == Globals.BallColor.GREEN
    }
}