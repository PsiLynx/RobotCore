package org.firstinspires.ftc.teamcode.subsystem;

import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.component.Motor;

public class Drivetrain extends Subsystem{
    private Motor frontLeft;
    private Motor frontRight;
    private Motor backRight;
    private Motor backLeft;
    private ThreeDeadWheelLocalizer localizer;
    private final double ticksPerRev = 1;
    private final double wheelRadius = 1;
    private final double inchesPerTick = ticksPerRev * 2 * Math.PI * wheelRadius;
    public Drivetrain(HardwareMap hardwareMap){
        super(hardwareMap);
        frontLeft = new Motor("frontLeft", hardwareMap, 312);
        frontRight = new Motor("frontRight", hardwareMap, 312);
        backRight = new Motor("backRight", hardwareMap, 312);
        backLeft = new Motor("backLeft", hardwareMap, 312);

        localizer = new ThreeDeadWheelLocalizer(hardwareMap, inchesPerTick);
        Twist2dDual<Time> test = localizer.update();
        test.

    }
}
