package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.component.Motor;

public class Drivetrain extends Subsystem{
    private final Motor frontLeft;
    private final Motor frontRight;
    private final Motor backRight;
    private final Motor backLeft;
    private final ThreeDeadWheelLocalizer localizer;
    private final double ticksPerRev = 1;
    private final double wheelRadius = 1;
    private final double inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius;

    private Pose2d location = new Pose2d(0, 0, 0);
    public Drivetrain(HardwareMap hardwareMap){
        super(hardwareMap);
        frontLeft = new Motor("frontLeft", hardwareMap, 312);
        frontRight = new Motor("frontRight", hardwareMap, 312);
        backRight = new Motor("backRight", hardwareMap, 312);
        backLeft = new Motor("backLeft", hardwareMap, 312);

        localizer = new ThreeDeadWheelLocalizer(hardwareMap, inchesPerTick);

    }

    public void setPoseEstimate(Pose2d location){
        this.location = location;
    }

    /**
     * calls hardware reads, use once per loop
     */
    public void update(){
        Twist2dDual<Time> twist = localizer.update();
        double x       = location.position.x;
        double y       = location.position.y;
        double heading = location.heading.toDouble();

        x += twist.line.x.get(0);
        y += twist.line.x.get(1);
        heading += twist.angle.get(0);

        this.location = new Pose2d(x, y, heading);

    }

    public Pose2d getPoseEstimate(){
        return location;
    }

    public void setWeightedDrivePower(Pose2d power){
        double drive  = power.position.x;
        double strafe = power.position.y;
        double turn   = power.heading.toDouble();

        setWeightedDrivePower(drive, strafe, turn);
    }

    public void setWeightedDrivePower(double drive, double strafe, double turn){
        double lfPower = drive + strafe + turn;
        double rfPower = drive - strafe - turn;
        double rbPower = drive + strafe - turn;
        double lbPower = drive - strafe + turn;

        double max = Math.max(
                Math.max(lfPower, rfPower),
                Math.max(rbPower, lbPower)
        );
        if(max > 1){
            lfPower /= max;
            rfPower /= max;
            rbPower /= max;
            lbPower /= max;
        }

        frontLeft.setPower(lfPower);
        frontRight.setPower(rfPower);
        backRight.setPower(rbPower);
        backLeft.setPower(lbPower);
    }
}
