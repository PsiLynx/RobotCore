package org.ftc3825.util

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.util.Globals.State.Testing
import kotlin.math.abs
import kotlin.math.min

open class TestClass {
    val hardwareMap = FakeHardwareMap
    init {
        GlobalHardwareMap.init(hardwareMap)
        CommandScheduler.init(FakeHardwareMap)

        Globals.state = Testing
        Globals.timeSinceStart = 0.0
        CommandScheduler.reset()

        CommandScheduler.update()
        CommandScheduler.update()

        FakeHardwareMap.allDeviceMappings.forEach {mapping ->
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
}