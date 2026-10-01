package org.firstinspires.ftc.teamcode.subsystem.tankDrivetrain

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.PI
import kotlin.math.sign

class RealIo : RealSubsystemIo(), Io {

    private val frontLeft  = HardwareMap.frontLeft (FORWARD)
    private val backLeft   = HardwareMap.backLeft  (FORWARD)
    private val frontRight = HardwareMap.frontRight(REVERSE)
    private val backRight  = HardwareMap.backRight (REVERSE)

    init {
        motors.forEach {
            it.useInternalEncoder(384.5, millimeters(104))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override val powers get() = ChassisSpeeds(
        0.0,
        ( frontRight.power + frontLeft.power ) / 2,
        ( frontRight.power - frontLeft.power ) / 2,
    )

    override var position: Pose2D
        get() = octoQuad.position.vector + octoQuad.position.heading % (2*PI)
        set(value) = octoQuad.setPos(value)


    override val velocity: Pose2D
        get() = octoQuad.velocity

    override fun resetLocalizer() = octoQuad.resetInternals()

    private val octoQuad = HardwareMap.octoQuad(
        xPort = 0,
        yPort = 1,
        ticksPerMM = 2000 / (32 * PI),
        offset = Vector2D(
            x = -54.0,
            y = -82.0,
        ),
        xDirection = FORWARD,
        yDirection = FORWARD,
        headingScalar = 1.0127
    )

    override val components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
        octoQuad
    )


    override fun differentialPowers(
        left: Double,
        right: Double,
        feedForward: Double,
        comp: Boolean,
    ){
        var leftPower = left
        var rightPower = right

        leftPower  += feedForward * leftPower.sign
        rightPower += feedForward * rightPower.sign


        val max = maxOf(leftPower, rightPower)

        if (max > 1) {
            leftPower /= max
            rightPower /= max
        }

        if(comp){
            frontLeft .compPower( leftPower )
            backLeft .compPower( leftPower )
            frontRight.compPower( rightPower )
            backRight.compPower( rightPower )
        } else {
            frontLeft .power = leftPower
            backLeft .power = leftPower
            frontRight.power = rightPower
            backRight.power = rightPower
        }
    }

}