package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.command.internal.GlobalHardwareMap
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
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
    velocityInterval: Int = 25
):
    Component {
    override var lastWrite = LastWrite.empty()
    override val hardwareDevice = GlobalHardwareMap.get(
        OctoQuadFWv3::class.java,
        name
    )

    var startPos = Pose2D(0, 0, PI / 2)

    lateinit var position: Pose2D
        private set

    lateinit var velocity: Pose2D
        internal set

    private var ocPos = Pose2D()
    private var ocVel = Pose2D()

    init {
        val data = hardwareDevice.readLocalizerData()
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

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetLocalizerAndCalibrateIMU()
    }

    override fun resetInternals() {
        hardwareDevice.resetLocalizerAndCalibrateIMU()
        update()
        startPos = Pose2D(0, 0, PI / 2)
        update()
    }
    override fun update(deltaTime: Double) {
        val data = hardwareDevice.readLocalizerData()

        ocPos = data.position
        ocVel = data.velocity

        velocity = (
            if(data.crcOk) ocVel rotatedBy startPos.heading
            else velocity
        )

        position =
            if(data.crcOk) ( ocPos rotatedBy startPos.heading ) + startPos
            else position + ( velocity * deltaTime )

    }

}
