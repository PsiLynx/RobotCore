package org.teamcode.sim

abstract class FunctionOutput(var output: Double, vararg var inputs: Double){
    override fun toString(): String {
        var str = ""
        inputs.forEach { str += "$it, " }

        str += output
        return str

    }
}