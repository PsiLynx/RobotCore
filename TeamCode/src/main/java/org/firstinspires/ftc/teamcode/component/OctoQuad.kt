package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.DeviceTimes
import org.firstinspires.ftc.teamcode.logging.Input
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

class OctoQuad(
    override val hardwareDevice: OctoQuadFWv3,
    override val uniqueName: String,
    xPort: Int,
    yPort: Int,
    ticksPerMM: Double,
    offset: Vector2D,
    xDirection: Component.Direction,
    yDirection: Component.Direction,
    headingScalar: Double,
    override var priority: Double,
    velocityInterval: Int = 25
): Component(), Input {
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

    override fun getRealValue() = arrayOf(
        data.position.x,
        data.position.y,
        data.position.heading.toDouble(),
        data.velocity.x,
        data.velocity.y,
        data.velocity.heading.toDouble(),
        if(data.crcOk) 1.0 else 0.0
    )
    override fun update(deltaTime: Double) {

        ocPos = Pose2D(
            getValue()[0],
            getValue()[1],
            getValue()[2],
        )
        ocVel = Pose2D(
            getValue()[3],
            getValue()[4],
            getValue()[5],
        )
        crcOk = getValue()[6] == 1.0

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
