package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

class OctoQuad(
    private val deviceSupplier: () -> OctoQuadFWv3?,
    val uniqueName: String,
    xPort: Int,
    yPort: Int,
    ticksPerMM: Double,
    offset: Vector2D,
    xDirection: Component.Direction,
    yDirection: Component.Direction,
    headingScalar: Double,
    override var priority: Double,
    velocityInterval: Int = 25
): Component() {

    private var _hwDeviceBacker: OctoQuadFWv3? = null
    override val hardwareDevice: OctoQuadFWv3 get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    override val ioOpTime = DeviceTimes.octoquad

    var startPos = Pose2D(0, 0, PI / 2)

    var data = hardwareDevice.readLocalizerData()

    var position: Pose2D
        private set

    var velocity: Pose2D
        internal set

    private var ocPos = Pose2D()
    private var ocVel = Pose2D()
    private var crcOk = true

    init {
        position = data.position
        velocity = data.velocity
        hardwareDevice.setAllLocalizerParameters(
            xPort,
            yPort,
            ticksPerMM.toFloat(),
            ticksPerMM.toFloat(),
            offset.x.toFloat(),
            offset.y.toFloat(),
            headingScalar.toFloat(),
            velocityInterval
        )
    }

    override fun ioOp(){ data = hardwareDevice.readLocalizerData() }

    override fun resetInternals() {
        hardwareDevice.resetLocalizerAndCalibrateIMU()
        update(0.0)
        startPos = Pose2D(0, 0, PI / 2)
        update(0.0)
    }

    override fun update(deltaTime: Double) {

        ocPos = data.position
        ocVel = data.velocity

        velocity = (
            if(crcOk) ocVel rotatedBy Rotation2D(PI / 2)
            else velocity
        )

        position =
            if(crcOk) ( ocPos rotatedBy Rotation2D(PI / 2) ) + startPos
            else position + ( velocity * deltaTime )

    }

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetLocalizerAndCalibrateIMU()
    }

}
