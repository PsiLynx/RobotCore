package org.firstinspires.ftc.teamcode.component;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motor {
    static int BRAKE = 0;
    static int FLOAT = 1;
    static int UNKNOWN = 2;
    static DcMotor.ZeroPowerBehavior[] zeroPowerBehaviors = {
            DcMotor.ZeroPowerBehavior.BRAKE,
            DcMotor.ZeroPowerBehavior.FLOAT,
            DcMotor.ZeroPowerBehavior.UNKNOWN};
    int rpm;
    double gearRatio;
    double Kstatic;
    String name;
    HardwareMap hardwareMap;
    double lastWrite = 0;
    DcMotor motor;

    public Motor(String name, HardwareMap hardwareMap){
        this.name = name;
        this.hardwareMap = hardwareMap;

        this.motor = hardwareMap.get(DcMotor.class, name);
    }

    public Motor(String name, HardwareMap hardwareMap, double kstatic){
        this.name = name;
        this.hardwareMap = hardwareMap;
        this.Kstatic = kstatic;

        this.motor = hardwareMap.get(DcMotor.class, name);

    }

    public Motor(String name, HardwareMap hardwareMap, int rpm, double kstatic){
        this.name = name;
        this.hardwareMap = hardwareMap;
        this.rpm = rpm;
        this.Kstatic = kstatic;

        this.motor = hardwareMap.get(DcMotor.class, name);

    }

    public void setPower(double speed){
        if (speed == lastWrite){
            return;
        }
        speed = (1 - Kstatic) * speed + Kstatic;
        motor.setPower(speed);
    }
    public int getPositsion(){
        return motor.getCurrentPosition();
    }

    public void setZeroPowerBehavior(int behavior){
        motor.setZeroPowerBehavior(zeroPowerBehaviors[behavior]);
    }

}
