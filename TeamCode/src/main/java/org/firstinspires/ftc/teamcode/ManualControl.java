package org.firstinspires.ftc.teamcode;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.FtcDashboard;

@TeleOp(name = " Manual Control")
public class ManualControl extends OpMode {
    public ManualControl() { super(); }

    @Override
    public void init() {
        hardwareMap.dcMotor.entrySet().forEach( (entry) -> {
            DcMotor motor = entry.getValue();
            FtcDashboard.getInstance().addConfigVariable(
                " Motors",
                entry.getKey(),
                new Provider(motor::getPower, motor::setPower)
            );
        });
        hardwareMap.servo.entrySet().forEach( (entry) -> {
            Servo servo =  entry.getValue();
            FtcDashboard.getInstance().addConfigVariable(
                    " Servos",
                    entry.getKey(),
                    new Provider(servo::getPosition, servo::setPosition)
            );
        });
        hardwareMap.crservo.entrySet().forEach( (entry) -> {
            CRServo CRServo =  entry.getValue();
            FtcDashboard.getInstance().addConfigVariable(
                    " CRServos",
                    entry.getKey(),
                    new Provider(CRServo::getPower, CRServo::setPower)
            );
        });
    }

    @Override
    public void loop() {

    }
    private class Provider implements ValueProvider<Double>{
        private final DoubleSupplier get;
        private final DoubleConsumer set;
        protected Provider(DoubleSupplier get, DoubleConsumer set) {
            this.get = get;
            this.set = set;
        }
        @Override public Double get() { return get.getAsDouble(); }
        @Override public void set(Double value) { set.accept(value); }
    }
}
