package org.firstinspires.ftc.teamcode.component;


import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Servo {
    String name;
    HardwareMap hardwareMap;
    double min; //angle
    double max; //angle
    double lastWrite;

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


    void setPosition(double pos){
        if(pos != lastWrite) {
            servo.setPosition(pos);
        }
    }
    void setAngle(double angle){
        if ( angle >= min && angle <= max){
            double pos = (angle - min) / max;
            setPosition(pos);
        }

    }
    double getPosition(){
        return lastWrite;
    }

}
