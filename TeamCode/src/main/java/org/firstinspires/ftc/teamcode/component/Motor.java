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
    Encoder encoder;
    double ticksPerRev = 1;
    double wheelRadius = 1;

    public Motor(String name, HardwareMap hardwareMap, int rpm){
        this.name = name;
        this.hardwareMap = hardwareMap;
        this.rpm = rpm;
        this.ticksPerRev = 28 * 6000.0 / rpm;

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
