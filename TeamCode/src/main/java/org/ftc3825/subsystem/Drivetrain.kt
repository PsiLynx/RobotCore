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
import org.ftc3825.component.IMU
import org.ftc3825.util.Rotation2D

object Drivetrain : Subsystem<Drivetrain>() {
    private val parYTicks = 2200
    private val perpXTicks = 1600
    private val ticksPerRev = 2000.0
    private val inPerTick = 48 * PI / ticksPerRev / 25.4

    val frontLeft  = Motor(flMotorName, 312, REVERSE)
    val frontRight = Motor(frMotorName, 312, FORWARD)
    val backLeft   = Motor(blMotorName, 312, REVERSE)
    val backRight  = Motor(brMotorName, 312, FORWARD)
    val imu = IMU("imu")

    override var motors = arrayListOf(frontLeft, backLeft, backRight, frontRight)
    var encoders = arrayListOf(
            Encoder(motors[0].motor, ticksPerRev, reversed = -1),
            Encoder(motors[1].motor, ticksPerRev)
    )

    var position = Pose2D()
    var delta = Pose2D()

    init {
        this.motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        imu.configureOrientation(
            usb = IMU.Direction.UP,
            logo = IMU.Direction.RIGHT
        )

        //Telemetry.addData("delta") { delta.toString() }
    }

    override fun update(deltaTime: Double) {
        motors.forEach   { it.update(deltaTime) }
        encoders.forEach { it.update()          }
        imu.update()
        updateOdo()
    }

    fun driveFeildCentric(power: Pose2D){
        setWeightedDrivePower(
            power.vector.rotatedBy(position.heading) + Rotation2D(power.heading)
        )
    }

    fun setWeightedDrivePower(power: Pose2D) {
        val drive = power.x
        val strafe = power.y
        val turn = power.heading
        setWeightedDrivePower(drive, strafe, turn)
    }

    fun setWeightedDrivePower(drive: Double, strafe: Double, turn: Double) {
        var lfPower = drive + strafe - turn
        var rfPower = drive - strafe + turn
        var rbPower = drive + strafe + turn
        var lbPower = drive - strafe - turn
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
            var par = encoders[0]
            var perp = encoders[1]

            val deltaR = imu.delta.theta
            val deltaX = ( perp.delta - deltaR * perpXTicks ) * inPerTick
            val deltaY = ( par.delta + deltaR * parYTicks ) * inPerTick

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
        position.heading = imu.yaw.theta
    }
}
