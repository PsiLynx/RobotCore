package org.firstinspires.ftc.teamcode.component;


import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Servo {
    String name;
    HardwareMap hardwareMap;
    double min; //angle
    double max; //angle
    double lastWrite;
    public static final double epsilon = 0.005;

    com.qualcomm.robotcore.hardware.Servo servo;

    public Servo(String name, HardwareMap hardwareMap){
        this.name = name;
        this.hardwareMap = hardwareMap;

        this.servo = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, name);
    }

    public Servo(String name, HardwareMap hardwareMap, double min, double max){
        this.name = name;
        this.hardwareMap = hardwareMap;
        this.min = min;
        this.max = max;
    }


    public void setPosition(double pos){
        if(Math.abs(pos - lastWrite) <= epsilon) {
            return;
        }
        servo.setPosition(pos);
        lastWrite = pos;
    }
    public void setAngle(double angle){
        if ( angle >= min && angle <= max){
            double pos = (angle - min) / max; //lerp from min to max
            setPosition(pos);
        }

    }
    public double getPosition(){
        return lastWrite;
    }

}
