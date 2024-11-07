package org.ftc3825.component

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import org.ftc3825.util.radians
import kotlin.math.abs

import com.qualcomm.robotcore.hardware.Servo as HardwareServo

/**
 * @param min the minimum angle of the servo, corresponding to position = 0.0
 * @param max the maximum angle of the servo, corresponding to position = 1.0
 */
class Servo(name: String, val min: Double = 0.0, val max: Double = radians(5.236) /* 300 degrees in radians */) {
    private var lastWrite = 0.0
    val servo: HardwareServo = GlobalHardwareMap.get(HardwareServo::class.java, name)
    //hardwareServo is an alias

    fun setAngle(angle: Double) {
        if (angle in min..max) {
            val pos = (angle - min) / ( max - min )//lerp from min to max
            position = pos
        }
    }

    var position: Double
        get() = lastWrite
        set(pos) {
            if ( abs(pos - lastWrite) <= EPSILON){
                return
            }

            //CommandScheduler.updatesPerLoop ++
            servo.position = pos
            lastWrite = pos
        }

    companion object {
        const val EPSILON = 0.001 // goBilda torque servo deadband
    }
}
