package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver
import org.firstinspires.ftc.teamcode.util.GoBildaPinpointDriver.GoBildaOdometryPods
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import kotlin.math.PI

class Pinpoint(name: String, override val priority: Double): Component() {
    override val ioOpTimeMs = DeviceTimes.pinpoint
    override val hardwareDevice = GlobalHardwareMap.get(
        GoBildaPinpointDriver::class.java,
        name
    )

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


    override fun resetInternals() {
        hardwareDevice.resetPosAndIMU()
        update()
        startPos = Pose2D(0, 0, PI / 2)
        update()
    }
    override fun update(deltaTime: Double) {
        hardwareDevice.update()

        ppPos = hardwareDevice.position
        ppVel = hardwareDevice.velocity

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
