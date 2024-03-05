package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.component.Servo;

public class Claw extends Subsystem{
    Servo left;
    Servo right;
    double open = 1;
    double closed = 0;
    public Claw(HardwareMap hardwareMap){
        super();
        left = new Servo("left", hardwareMap);
        right = new Servo("right", hardwareMap);

    }
    public void openLeft(){
        left.setPosition(open);
    }
    public void openRight(){
        right.setPosition(open);
    }

    public void closeLeft(){
        left.setPosition(closed);
    }
    public void closeRight(){
        right.setPosition(closed);
    }
}
