package org.ftc3825.subsystem

import kotlin.math.PI
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Globals
import org.ftc3825.component.Motor
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName
import org.ftc3825.component.Encoder
import org.ftc3825.fakehardware.FakeMotor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.command.internal.CommandScheduler

object Drivetrain : Subsystem<Drivetrain>() {
    private val par1YTicks = 2329.2
    private val par2YTicks = -2329.2
    private val perpXTicks = 1525.77
    private val ticksPerRev = 1999.0
    private val inPerTick = 48 * PI / ticksPerRev / 25.4

    val frontLeft  = Motor(flMotorName, 312, FORWARD)
    val frontRight = Motor(frMotorName, 312, REVERSE)
    val backLeft   = Motor(blMotorName, 312, FORWARD)
    val backRight  = Motor(brMotorName, 312, REVERSE)
    override var motors = arrayListOf(frontLeft, backLeft, backRight, frontRight)
    var encoders = arrayListOf(
            Encoder(motors[0].motor, ticksPerRev, reversed = -1),
            Encoder(motors[1].motor, ticksPerRev),
            Encoder(motors[3].motor, ticksPerRev)
    )

    var position = Pose2D()
    var delta = Pose2D()

    init {
        this.motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }

        Telemetry.addData("delta") { delta.toString() }
    }

    override fun update(deltaTime: Double) {
        motors.forEach   { it.update(deltaTime) }
        encoders.forEach { it.update()          }
        updateOdo()
    }

    fun setWeightedDrivePower(power: Pose2D) {
        val drive = power.x
        val strafe = power.y
        val turn = power.heading
        setWeightedDrivePower(drive, strafe, turn)
    }

    fun setWeightedDrivePower(drive: Double, strafe: Double, turn: Double) {
        var lfPower = drive + strafe + turn
        var rfPower = drive - strafe - turn
        var rbPower = drive + strafe - turn
        var lbPower = drive - strafe + turn
        val max = maxOf(lfPower, rfPower, rbPower, lbPower)
        if (max > 1) {
            lfPower /= max
            rfPower /= max
            rbPower /= max
            lbPower /= max
        }
        frontLeft.setPower(lfPower)
        frontRight.setPower(rfPower)
        backRight.setPower(rbPower)
        backLeft.setPower(lbPower)
    }

    fun updateOdo(){
        if(Globals.state == Globals.State.Running){
            var par1 = encoders[0]
            var perp = encoders[1]
            var par2 = encoders[2]

            val deltaX = (
                    (par1YTicks * par2.delta - par2YTicks * par1.delta)
                    / (par1YTicks - par2YTicks)
            ) * inPerTick

            val deltaY = (
                    perpXTicks / (par1YTicks - par2YTicks)
                    * (par2.delta - par1.delta)
                    + perp.delta
            ) * inPerTick
            
            val deltaR = (par1.delta - par2.delta) / (par1YTicks - par2YTicks)
            
            delta = Pose2D(deltaX, deltaY, deltaR)
        }
        else{
            val fl = (frontLeft.motor as FakeMotor).speed
            val fr = (frontRight.motor as FakeMotor).speed * -1 //reversed motor
            val br = (backRight.motor as FakeMotor).speed * -1

            val drive = (fl + fr) / 2.0
            val strafe = ( (fl + br) - drive * 2 ) / 2.0
            val turn = fl - drive - strafe

            delta = Pose2D(drive, strafe, turn)
        }
        position.applyToEnd(delta)
    }
}
