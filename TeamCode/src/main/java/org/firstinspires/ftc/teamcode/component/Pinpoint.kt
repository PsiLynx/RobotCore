package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.logging.Input
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver.GoBildaOdometryPods
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import kotlin.math.PI

class Pinpoint(
    override val hardwareDevice: GoBildaPinpointDriver,
    override val uniqueName: String,
    override var priority: Double
): Component(),
    Input {
    override val ioOpTime = HardwareMap.DeviceTimes.pinpoint

    var startPos = Pose2D(0, 0, PI / 2)

    var position: Pose2D = hardwareDevice.position!!
        private set

    var velocity = hardwareDevice.velocity!!
        internal set

    var posBad = false
        internal set
    var velBad = false
        internal set

    private var ppPos = Pose2D()
    private var ppVel = Pose2D()

    var xEncoderOffset: Double = 0.0
        set(value){
            field = value
            hardwareDevice.setOffsets(value, yEncoderOffset)
        }
    var yEncoderOffset: Double = 0.0
        set(value){
            field = value
            hardwareDevice.setOffsets(xEncoderOffset, value)
        }
    var xEncoderDirection: Direction = Direction.FORWARD
        set(value) {
            field = value
            hardwareDevice.setEncoderDirections(value, yEncoderDirection)
        }
    var yEncoderDirection: Direction = Direction.FORWARD
        set(value) {
            field = value
            hardwareDevice.setEncoderDirections(xEncoderDirection, value)
        }
    var podType: GoBildaOdometryPods = GoBildaOdometryPods.goBILDA_SWINGARM_POD
        set(value) {
            field = value
            hardwareDevice.setEncoderResolution(value)
        }

    override fun ioOp() { hardwareDevice.update() }

    override fun getRealValue() = arrayOf(
        hardwareDevice.position.x,
        hardwareDevice.position.y,
        hardwareDevice.position.heading.toDouble(),
        hardwareDevice.velocity.x,
        hardwareDevice.velocity.y,
        hardwareDevice.velocity.heading.toDouble(),
    )

    override fun resetInternals() {
        hardwareDevice.resetPosAndIMU()
        update(0.0)
        startPos = Pose2D(0, 0, PI / 2)
        update(0.0)
    }
    override fun update(deltaTime: Double) {
        ppPos = Pose2D(
            getValue()[0],
            getValue()[1],
            getValue()[2],
        )
        ppVel = Pose2D(
            getValue()[3],
            getValue()[4],
            getValue()[5],
        )

        posBad = (
               ppPos.x.isNaN()
            || ppPos.y.isNaN()
            || ppPos.heading.toDouble().isNaN()
        )
        velBad = (
               ppVel.x.isNaN()
            || ppVel.y.isNaN()
            || ppVel.heading.toDouble().isNaN()
        )

        velocity = (
            if(velBad) velocity
            else ppVel rotatedBy startPos.heading
        )

        position =
            if(posBad) position + ( velocity * deltaTime )
            else ( ppPos rotatedBy startPos.heading ) + startPos

//        position = Pose2D(
//            position.vector,
//            Rotation2D(position.heading.toDouble() % 2 * PI)
//        )
    }

    fun resetHeading() = hardwareDevice.recalibrateIMU()

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetPosAndIMU()
    }
}