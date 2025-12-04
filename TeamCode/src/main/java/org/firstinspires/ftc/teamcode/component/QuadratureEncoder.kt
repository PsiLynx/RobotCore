package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.openftc.easyopencv.OpenCvWebcam
import java.util.function.DoubleSupplier

class QuadratureEncoder(
    private val deviceSupplier: () -> DcMotor?,
    override var direction: Component.Direction,
    override var ticksPerRev: Double,
    override var wheelRadius: Double,
): Encoder(){

    private var _hwDeviceBacker: DcMotor? = null
    val hardwareDevice: DcMotor get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }
    override val posSupplier = DoubleSupplier {
        hardwareDevice.currentPosition.toDouble()
    }
    override val velSupplier =  { unused: Double ->
        val value = (hardwareDevice as DcMotorEx).velocity
        value
    }
}