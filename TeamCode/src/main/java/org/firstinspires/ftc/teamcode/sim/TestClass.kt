package org.firstinspires.ftc.teamcode.sim

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.gvf.GVFConstants
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.Globals
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.HardwareMapWrapper
import org.psilynx.psikit.ftc.OpModeControls
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import java.lang.reflect.Field
import kotlin.math.abs
import kotlin.math.min


open class TestClass {
    init {
        val hardwareMap = HardwareMapWrapper(FakeHardwareMap)
        println("init test class")
        Globals.running = false
        Globals.unitTesting = true

        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, FakeTimer())

        FakeMotor.fromDcMotor(Flywheel.motorLeft.hardwareDevice as DcMotor).let {
            it.maxVelocityInTicksPerSecond = (
                1.0
                / (Flywheel.motorLeft.encoder?.inPerTick ?: 0.0)
            ).toInt()

            it.maxAccel = 1.5
        }

        CommandScheduler.reset()
        CommandScheduler.update()
        CommandScheduler.update()

        Logger.reset()
        Logger.setTimeSource(FakeTimer::time)

        injectConstants()

        FakeHardwareMap.allDeviceMappings.forEach { mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }

        OpModeControls.started = true

    }

    /**
     * resolve the fake motor wrapped somewhere in the motor
     */
    fun fakeMotor(motor: DcMotor): FakeMotor{
        var _motor = motor
        repeat(10) {
            if(_motor is FakeMotor) return _motor
            else if(motor is MotorWrapper){
                val field = motor::class.java.getDeclaredField("device")
                field.isAccessible = true
                _motor = field.get(motor) as DcMotor
            }
        }
        error("motor was none of FakeMotor or MotorWrapper")
    }

    fun assertEqual(x: Any, y:Any) {
        if(x != y){
            throw AssertionError("x: $x != y: $y")
        }
    }

    fun endOpMode(opMode: OpMode){
        var current: Class<*> = opMode::class.java
        var field: Field? = null
        while (field == null) {
            try {
                println(current.simpleName)
                field = current.getDeclaredField("stopRequested")
            } catch (_: NoSuchFieldException) {
                current = current.superclass!!
            }
        }
        field!!.isAccessible = true
        field.set(opMode, true)
    }
    fun assertWithin(value: Number, epsilon: Number){
        if(abs(value.toDouble()) > epsilon.toDouble()){
            throw AssertionError("| $value | > $epsilon!")
        }
    }

    fun assertGreater(larger: Number, smaller: Number): Boolean{
        if(larger.toDouble() <= smaller.toDouble()){
            throw AssertionError("$larger <= $smaller, first number should be larger!")
        }
        else{
            return true
        }
    }
    infix fun Number.isGreaterThan(other: Number) = assertGreater(this, other)

    fun unit() { }
    fun diff(str1: String, str2: String): String{
        var output = ""
        val length = min(str1.length, str2.length)

        for( i in 0..<length){
            if(str1[i] != str2[i]) output += str1[i]
        }
        return output
    }
    private fun injectConstants(){

        GVFConstants.SPLINE_RES = FakeGVFConstants.SPLINE_RES

        GVFConstants.FEED_FORWARD = FakeGVFConstants.FEED_FORWARD

        GVFConstants.DRIVE_P = FakeGVFConstants.DRIVE_P

        GVFConstants.DRIVE_D = FakeGVFConstants.DRIVE_D

        GVFConstants.TRANS_P = FakeGVFConstants.TRANS_P

        GVFConstants.TRANS_D = FakeGVFConstants.TRANS_D

        GVFConstants.HEADING_P = FakeGVFConstants.HEADING_P

        GVFConstants.HEADING_D = FakeGVFConstants.HEADING_D

        GVFConstants.CENTRIPETAL = FakeGVFConstants.CENTRIPETAL

        GVFConstants.PATH_END_T = FakeGVFConstants.PATH_END_T

        GVFConstants.MAX_VELO = FakeGVFConstants.MAX_VELO

        GVFConstants.USE_COMP = FakeGVFConstants.USE_COMP

        GVFConstants.USE_CENTRIPETAL = FakeGVFConstants.USE_CENTRIPETAL

    }
}