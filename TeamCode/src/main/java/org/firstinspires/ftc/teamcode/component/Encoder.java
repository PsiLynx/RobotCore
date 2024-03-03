package org.firstinspires.ftc.teamcode.component;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Encoder {
    double ticksPerRevolution;
    double gearRatio = 1;
    double wheelRadius;
    double offset;
    DcMotor motor;
    public Encoder(DcMotor motor, double ticksPerRevolution, double wheelRadius){
        this.motor = motor;
        this.ticksPerRevolution = ticksPerRevolution;
        this.wheelRadius = wheelRadius;
    }

    public Encoder(DcMotor motor, double ticksPerRevolution, double wheelRadius, double gearRatio){
        this.motor = motor;
        this.ticksPerRevolution = ticksPerRevolution;
        this.wheelRadius = wheelRadius;
        this.gearRatio = gearRatio;
    }
    double ticksToInches(double ticks){
        return (ticks + offset)
                / ticksPerRevolution
                * (wheelRadius / gearRatio)
                * 2*Math.PI;
    }
    public double getPosition(){
        return ticksToInches(motor.getCurrentPosition());
    }
    public double getAngle(){
        double ticks = motor.getCurrentPosition();
        return (ticks + offset)
                / ticksPerRevolution
                * 360
                % 360;
    }
    public void set(double inches){
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        offset += inches / ( 2 * Math.PI * (wheelRadius / gearRatio) ) * ticksPerRevolution;
    }
    public void setAngle(double angle){
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        offset = ticksPerRevolution / ( angle / 360 );
    }

}
