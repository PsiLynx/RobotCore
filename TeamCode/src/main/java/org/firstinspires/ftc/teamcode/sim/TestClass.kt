package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.gvf.GVFConstants
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.State.Testing
import kotlin.math.abs
import kotlin.math.min


open class TestClass {
    val hardwareMap = FakeHardwareMap
    init {
        HardwareMap.init(hardwareMap)
        CommandScheduler.init(hardwareMap, FakeTimer())
        HWQue.init(FakeTimer())

        Globals.state = Testing
        CommandScheduler.reset()

        CommandScheduler.update()
        CommandScheduler.update()

        injectConstants()

        FakeHardwareMap.allDeviceMappings.forEach { mapping ->
            mapping.forEach {
                it.resetDeviceConfigurationForOpMode()
            }
        }

    }

        fun assertEqual(x: Any, y:Any) {
            if( !x.equals(y) ){
                throw AssertionError("x: $x != y: $y")
            }
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

        GVFConstants.HEADING_POW = FakeGVFConstants.HEADING_POW

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