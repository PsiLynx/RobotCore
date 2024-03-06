package org.firstinspires.ftc.teamcode.component;


import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMU {
    public static AngleUnit DEGREES = AngleUnit.DEGREES;
    public static AngleUnit RADIANS = AngleUnit.RADIANS;

    public static LogoFacingDirection[] logoDirections = {
            LogoFacingDirection.UP,
            LogoFacingDirection.DOWN,
            LogoFacingDirection.LEFT,
            LogoFacingDirection.RIGHT,
            LogoFacingDirection.FORWARD,
            LogoFacingDirection.BACKWARD};
    public static UsbFacingDirection[] USBDirections = {
            UsbFacingDirection.UP,
            UsbFacingDirection.DOWN,
            UsbFacingDirection.LEFT,
            UsbFacingDirection.RIGHT,
            UsbFacingDirection.FORWARD,
            UsbFacingDirection.BACKWARD};
    public static int UP      = 0;
    public static int DOWN    = 1;
    public static int LEFT    = 2;
    public static int RIGHT   = 3;
    public static int FORWARD = 4;
    public static int BACKWARD = 5;
    private com.qualcomm.robotcore.hardware.IMU imu;
    private AngleUnit unit = RADIANS;
    private double offset = 0;

    public IMU(String name, HardwareMap hardwareMap){
        this.imu = hardwareMap.get(com.qualcomm.robotcore.hardware.IMU.class, name);
    }

    public void setUnit(AngleUnit unit){
        if(unit == this.unit){
            return;
        }
        this.unit = unit;
        if(unit == DEGREES){
            offset = offset * 180 / Math.PI;
        }
        else{
            offset = offset / 180 * Math.PI;
        }
    }
    public void configureOrientation(int logoDirection, int USBDirection){
        imu.initialize(
                new com.qualcomm.robotcore.hardware.IMU.Parameters(new RevHubOrientationOnRobot(
                        logoDirections[logoDirection],
                        USBDirections[USBDirection]
                ))
        );
    }

    public double getPich(){
        return imu.getRobotYawPitchRollAngles().getPitch(unit);
    }
    public double getRoll(){
        return imu.getRobotYawPitchRollAngles().getRoll(unit);
    }
    public double getYaw(){
        return imu.getRobotYawPitchRollAngles().getYaw(unit) + offset;
    }
    public void resetYaw(double angle){
        imu.resetYaw();
        offset += angle;
    }
    public void resetYaw(){
        imu.resetYaw();
    }
}
