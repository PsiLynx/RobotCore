package org.firstinspires.ftc.teamcode.component;

public class Encoder {
    double ticksPerRevolution;
    double gearRatio = 1;
    double wheelRadius;
    double offset;
    //TODO: motor-y stuff to read the encoder

    Motor motor;
    public Encoder(Motor motor, double ticksPerRevolution, double wheelRadius){
        this.motor = motor;
        this.ticksPerRevolution = ticksPerRevolution;
        this.wheelRadius = wheelRadius;
    }

    public Encoder(Motor motor, double ticksPerRevolution, double wheelRadius, double gearRatio){
        this.motor = motor;
        this.ticksPerRevolution = ticksPerRevolution;
        this.wheelRadius = wheelRadius;
        this.gearRatio = gearRatio;
    }
    double ticksToInches(int ticks){
        return (ticks + offset)
                / ticksPerRevolution
                * (wheelRadius / gearRatio)
                * 2*Math.PI;
    }
    double getPosition(){
        //TODO: read position somehow
        return ticksToInches(motor.getPositsion());
    }
    void set(int ticks){
        //TODO: reset
        offset = ticks;
    }
    void setAngle(double angle){
        //TODO: reset
        offset = ticksPerRevolution / ( angle / 360 );
    }

}
