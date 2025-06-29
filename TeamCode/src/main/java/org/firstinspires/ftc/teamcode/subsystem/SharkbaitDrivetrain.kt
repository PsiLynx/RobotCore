package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor.ZeroPower.FLOAT
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.util.millimeters
import kotlin.math.sign

object SharkbaitDrivetrain : Subsystem<SharkbaitDrivetrain>() {

    private val frontLeft  = HardwareMap.frontLeft (FORWARD, 1.0, 1.0)
    private val frontRight = HardwareMap.frontRight(REVERSE, 1.0, 1.0)
    private val backLeft   = HardwareMap.backLeft  (FORWARD, 1.0, 1.0)
    private val backRight  = HardwareMap.backRight (REVERSE, 1.0, 1.0)

    override var components: List<Component> = arrayListOf<Component>(
        frontLeft,
        backLeft,
        backRight,
        frontRight,
    )

    var position = Pose2D()
    var lastPosition = Pose2D()
    var velocity = Pose2D()

    init {
        motors.forEach {
            it.useInternalEncoder(537.7 * 2, millimeters(96))
            it.setZeroPowerBehavior(FLOAT)
        }
    }

    override fun update(deltaTime: Double) {
        val flSpeed = frontLeft.velocity
        val frSpeed = frontRight.velocity
        val blSpeed = backLeft.velocity

        log("fl speed") value flSpeed
        log("fr speed") value frSpeed
        log("bl speed") value blSpeed

        lastPosition = position

        position.applyToEnd(
            Pose2D(
                x = ( + flSpeed - frSpeed - blSpeed ) / 3,
                y = ( flSpeed + frSpeed + blSpeed ) / 3,
                heading = (
                    - blSpeed
                    + frSpeed
                    + ( flSpeed + frSpeed + blSpeed ) / 3
                    - flSpeed
                ) / 4 * 0.00975
            ) * deltaTime
        )

        velocity = ( position - lastPosition ) / deltaTime

        log("position") value position
    }

    fun setWeightedDrivePower(
        drive: Double = 0.0,
        strafe: Double = 0.0,
        turn: Double = 0.0,
        feedForward: Double = 0.0,
        comp: Boolean = false
    ) {
        var flPower = drive + strafe * 1.1 - turn
        var frPower = drive - strafe * 1.1 + turn
        var brPower = drive + strafe * 1.1 + turn
        var blPower = drive - strafe * 1.1 - turn
        flPower += feedForward * flPower.sign
        frPower += feedForward * frPower.sign
        brPower += feedForward * brPower.sign
        blPower += feedForward * blPower.sign
        val max = maxOf(flPower, frPower, brPower, blPower)
        if (max > 1 + 1e-4) {

            flPower /= max
            frPower /= max
            blPower /= max
            brPower /= max
        }
        if(comp){
            frontLeft .compPower( flPower )
            frontRight.compPower( frPower )
            backLeft  .compPower( blPower )
            backRight .compPower( brPower )
        } else {
            frontLeft .power = flPower
            frontRight.power = frPower
            backRight .power = brPower
            backLeft  .power = blPower
        }
    }
}
