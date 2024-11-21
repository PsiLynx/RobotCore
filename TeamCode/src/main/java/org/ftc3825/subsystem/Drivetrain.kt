package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.component.Motor.Direction.FORWARD
import org.ftc3825.component.Motor.Direction.REVERSE
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.pedroPathing.follower.Follower
import org.ftc3825.pedroPathing.localization.Pose
import org.ftc3825.util.Globals
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.blMotorName
import org.ftc3825.util.brMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.frMotorName


object Drivetrain : Subsystem<Drivetrain>() {
    private val frontLeft  = Motor(flMotorName, 312, REVERSE)
    private val frontRight = Motor(frMotorName, 312, FORWARD)
    private val backLeft   = Motor(blMotorName, 312, REVERSE)
    private val backRight  = Motor(brMotorName, 312, FORWARD)

    val follower = Follower(GlobalHardwareMap.hardwareMap)

    var positionOffset: Pose2D = Pose2D()
        set(value) {
            follower.pose = Pose(
                value.x,
                value.y,
                value.heading
            )
        }

    override var components = arrayListOf<Component>(frontLeft, backLeft, backRight, frontRight)
//    var encoders = arrayListOf(
//            Encoder(components[0].hardwareDevice, ticksPerRev, reversed = -1),
//            Encoder(components[1].hardwareDevice, ticksPerRev)
//    )

    var position = Pose2D()
    var delta = Pose2D()

    init {
        this.components.forEach {
            if(it is Motor) {
                it.useInternalEncoder()
                it.hardwareDevice.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
            }
        }
//        imu.configureOrientation(
//            usb = IMU.Direction.UP,
//            logo = IMU.Direction.RIGHT
//        )
    }

    override fun update(deltaTime: Double) {
        components.forEach   { it.update(deltaTime) }
        //encoders.forEach { it.update()          }
//        imu.update()
        updateOdo()
        if(follower.currentPath != null) {
            follower.update()
        }
        else {
            follower.poseUpdater.update()
        }
    }

    fun driveFieldCentric(power: Pose2D){
        setWeightedDrivePower(
            power.vector.rotatedBy(follower.pose.heading) + Rotation2D(power.heading)
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

    fun setMotorPowers(
        leftFront: Double,
        leftRear: Double,
        rightFront: Double,
        rightRear: Double
    ){
        frontLeft.setPower(leftFront)
        frontRight.setPower(rightFront)
        backLeft.setPower(leftRear)
        backRight.setPower(rightRear)

    }
    fun setMotorPowers( powers: DoubleArray ){
        var leftFront = powers[0]
        var leftRear = powers[1]
        var rightFront = powers[2]
        var rightRear = powers[3]
        frontLeft.setPower(leftFront)
        frontRight.setPower(rightFront)
        backLeft.setPower(leftRear)
        backRight.setPower(rightRear)

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

        }
        else{
            val fl = (frontLeft.hardwareDevice as FakeMotor).speed
            val fr = (frontRight.hardwareDevice as FakeMotor).speed * -1 //reversed hardwareDevice
            val br = (backRight.hardwareDevice as FakeMotor).speed * -1

            val drive = (fl + fr) / 2.0
            val strafe = ( (fl + br) - drive * 2 ) / 2.0
            val turn = fl - drive - strafe

            delta = Pose2D(drive, strafe, turn)

            position.applyToEnd(delta)
            //position.heading = imu.yaw.theta
        }

    }
}
