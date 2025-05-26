package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

class OctoQuad(
    name: String,
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
    override val hardwareDevice = HardwareMap.get(
        OctoQuadFWv3::class.java,
        name
    )
    override val ioOpTime = DeviceTimes.octoQuad

    var startPos = Pose2D(0, 0, PI / 2)

    val data = hardwareDevice.readLocalizerData()

    var position: Pose2D
        private set

    var velocity: Pose2D
        internal set

    private var ocPos = Pose2D()
    private var ocVel = Pose2D()

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

    override fun ioOp(){ hardwareDevice.readLocalizerData() }

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
            if(data.crcOk) ocVel rotatedBy Rotation2D(PI / 2)
            else velocity
        )

        position =
            if(data.crcOk) ( ocPos rotatedBy Rotation2D(PI / 2) ) + startPos
            else position + ( velocity * deltaTime )

    }

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetLocalizerAndCalibrateIMU()
    }

}
