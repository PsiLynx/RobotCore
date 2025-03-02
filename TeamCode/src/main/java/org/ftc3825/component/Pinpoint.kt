package org.ftc3825.component

import kotlinx.coroutines.handleCoroutineException
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.util.GoBildaPinpointDriver.EncoderDirection
import org.ftc3825.util.GoBildaPinpointDriver.GoBildaOdometryPods
import org.ftc3825.component.Component.Direction
import org.ftc3825.fakehardware.FakePinpoint
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

class Pinpoint(name: String): Component {
    override var lastWrite = LastWrite.empty()
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
            hardwareDevice.setEncoderDirections(
                if(value == Direction.FORWARD) EncoderDirection.FORWARD
                else EncoderDirection.REVERSED,

                if(yEncoderDirection == Direction.FORWARD)
                    EncoderDirection.FORWARD
                else EncoderDirection.REVERSED,
            )
        }
    var yEncoderDirection: Direction = Direction.FORWARD
        set(value) {
            field = value
            hardwareDevice.setEncoderDirections(
                if(xEncoderDirection == Direction.FORWARD)
                    EncoderDirection.FORWARD
                else EncoderDirection.REVERSED,

                if(value == Direction.FORWARD) EncoderDirection.FORWARD
                else EncoderDirection.REVERSED

            )
        }
    var podType: GoBildaOdometryPods = GoBildaOdometryPods.goBILDA_SWINGARM_POD
        set(value) {
            field = value
            hardwareDevice.setEncoderResolution(value)
        }

    fun setStart(value: Pose2D) { startPos = value - position }

    override fun resetInternals() {
        hardwareDevice.resetPosAndIMU()
        update()
        println("reset: ")
        println( (hardwareDevice as FakePinpoint)._pos )
        println(hardwareDevice.position)
        println(position)
        println()
        startPos = Pose2D(0, 0, PI / 2)
        update()
    }
    override fun update(deltaTime: Double) {
        hardwareDevice.update()

        val ppPos = hardwareDevice.position
        val ppVel = hardwareDevice.velocity

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

    }

}
