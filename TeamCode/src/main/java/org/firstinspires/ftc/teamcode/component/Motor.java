package org.firstinspires.ftc.teamcode.component;

import java.lang.Math;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motor {
    public static final double epsilon = 0.005; //less than this and you don't write to the motors
    public static int BRAKE = 0;
    public static int FLOAT = 1;
    public static int UNKNOWN = 2;
    public static DcMotor.ZeroPowerBehavior[] zeroPowerBehaviors = {
            DcMotor.ZeroPowerBehavior.BRAKE,
            DcMotor.ZeroPowerBehavior.FLOAT,
            DcMotor.ZeroPowerBehavior.UNKNOWN};

    public static class Directions{
        public static final int FORWARD =  1;
        public static final int REVERSE = -1;
    }
    private int rpm;
    private double gearRatio;
    private double Kstatic;
    private String name;
    private HardwareMap hardwareMap;
    private double lastWrite = 0;
    private DcMotor motor;
    private Encoder encoder;
    private double ticksPerRev = 1;
    private double wheelRadius = 1;
    private int direction = 1;

    public Motor(String name, HardwareMap hardwareMap, int rpm){
        this.name = name;
        this.hardwareMap = hardwareMap;
        this.rpm = rpm;
        this.ticksPerRev = 28 * 6000.0 / rpm; //Nevrest motors have 6,000 rpm base and 28 ticks per revolution

        this.motor = hardwareMap.get(DcMotor.class, name);
    }

    public void setKstatic(double Kstatic){
        this.Kstatic = Kstatic;
    }
    public void setRpm(int rpm){
        this.rpm = rpm;
    }
    public void setGearRatio(double gearRatio){
        this.gearRatio = gearRatio;
    }
    public void useInternalEncoder(){
        this.encoder = new Encoder(this.motor, this.ticksPerRev, this.wheelRadius);
    }
    public void setWheelRadius(double radius){
        this.wheelRadius = radius;
    }
    public void setTicksPerRev(double ticksPerRev){
        this.ticksPerRev = ticksPerRev;
    }

    public int getPositsion(){
        return motor.getCurrentPosition();
    }

    public void setZeroPowerBehavior(int behavior){
        motor.setZeroPowerBehavior(zeroPowerBehaviors[behavior]);
    }
    public void setDirection(int direction){
        switch (direction){
            case Motor.Directions.FORWARD:{
                motor.setDirection(DcMotorSimple.Direction.FORWARD);
            }
            case Motor.Directions.REVERSE:{
                motor.setDirection(DcMotorSimple.Direction.REVERSE);
            }
            default:{
                break;
            }
        }
    }
    public void setPower(double speed){
        if (Math.abs(speed - lastWrite ) <  epsilon){
            return;
        }
        speed = (1 - Kstatic) * speed + Kstatic; //lerp from Kstatic to 1
        motor.setPower(speed);
    }

}
