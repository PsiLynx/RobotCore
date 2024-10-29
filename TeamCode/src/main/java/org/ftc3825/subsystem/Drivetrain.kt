package org.ftc3825.subsystem

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.ftc3825.component.Encoder
import org.ftc3825.component.IMU
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.util.Globals
import org.ftc3825.util.GoBildaPinpointDriver
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName


object Drivetrain : Subsystem<Drivetrain>() {
    private const val yOffset = 2200.0 // offset of strafe  pod in mm
    private const val xOffset = 1600.0 // offset of forward pod in mm
    /*
       [front of robot]
              | + y offset
              |
              |
              |         + x offset
    __________|_____________ [right of robot]
              |
              |
              |


     */

    val frontLeft  = Motor(flMotorName, 312, REVERSE)
    val frontRight = Motor(frMotorName, 312, FORWARD)
    val backLeft   = Motor(blMotorName, 312, REVERSE)
    val backRight  = Motor(brMotorName, 312, FORWARD)
    val imu = IMU("imu")

//    val pinpoint = hardwareMap.get(
//        GoBildaPinpointDriver::class.java, "odo"
//    )

    override var motors = arrayListOf(frontLeft, backLeft, backRight, frontRight)
//    var encoders = arrayListOf(
//            Encoder(motors[0].motor, ticksPerRev, reversed = -1),
//            Encoder(motors[1].motor, ticksPerRev)
//    )

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
//        pinpoint.setOffsets(xOffset, yOffset)
//        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
//        pinpoint.resetPosAndIMU()
    }

    override fun update(deltaTime: Double) {
        motors.forEach   { it.update(deltaTime) }
        //encoders.forEach { it.update()          }
        imu.update()
        updateOdo()
    }

    fun driveFeildCentric(power: Pose2D){
        setWeightedDrivePower(
            power.vector.rotatedBy(imu.yaw.theta) + Rotation2D(power.heading)
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
//            var par = encoders[0]
//            var perp = encoders[1]
//
//            val deltaR = imu.delta.theta
//            val deltaX = ( perp.delta - deltaR * perpXTicks ) * inPerTick
//            val deltaY = ( par.delta + deltaR * parYTicks ) * inPerTick

            // delta = Pose2D(deltaX, deltaY, deltaR)
//            pinpoint.update()
//
//            val pose = pinpoint.position!!
//            position = Pose2D(
//                pose.getX(DistanceUnit.INCH),
//                pose.getY(DistanceUnit.INCH),
//                pose.getHeading(AngleUnit.RADIANS)
//            )
        }
        else{
            val fl = (frontLeft.motor as FakeMotor).speed
            val fr = (frontRight.motor as FakeMotor).speed * -1 //reversed motor
            val br = (backRight.motor as FakeMotor).speed * -1

            val drive = (fl + fr) / 2.0
            val strafe = ( (fl + br) - drive * 2 ) / 2.0
            val turn = fl - drive - strafe

            delta = Pose2D(drive, strafe, turn)

            position.applyToEnd(delta)
            position.heading = imu.yaw.theta
        }

    }
}
