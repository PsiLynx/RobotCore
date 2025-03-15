package org.firstinspires.ftc.teamcode.util;

import android.content.Context;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;

public class DashHardwareMap extends HardwareMap {
    private final HardwareMap hardwareMap;
    public DashHardwareMap(HardwareMap hardwareMap, Context appContext, OpModeManagerNotifier notifier) {
        super(appContext, notifier);
        this.hardwareMap = hardwareMap;
    }
    @Override
    public <T> T get(Class<? extends T> classOrInterface, String deviceName){
        T device = hardwareMap.get(classOrInterface, deviceName);
        ValueProvider<Double> provider = null;
        if(classOrInterface == DcMotor.class){
            provider = new MotorValueProvider((DcMotor) device);
        }
        if(classOrInterface == Servo.class){
            provider = new ServoValueProvider((Servo) device);
        }
        if(classOrInterface == CRServo.class) {
            provider = new CRServoValueProvider((CRServo) device);
        }
        if(provider != null){
            FtcDashboard.getInstance().addConfigVariable(
                    classOrInterface.getSimpleName(),
                    deviceName,
                    provider
            );
        }
        return device;
    }
}
class ServoValueProvider implements ValueProvider<Double>{
    private final Servo servo;
    public ServoValueProvider(Servo servo){ this.servo = servo; }
    @Override public Double get() { return servo.getPosition(); }
    @Override public void set(Double value) { servo.setPosition(value); }
}
class MotorValueProvider implements ValueProvider<Double>{
    private final DcMotor motor;
    public MotorValueProvider(DcMotor motor){ this.motor = motor; }
    @Override public Double get() { return motor.getPower(); }
    @Override public void set(Double value) { motor.setPower(value); }
}
class CRServoValueProvider implements ValueProvider<Double>{
    private final CRServo CRServo;
    public CRServoValueProvider(CRServo CRServo){ this.CRServo = CRServo; }
    @Override public Double get() { return CRServo.getPower(); }
    @Override public void set(Double value) { CRServo.setPower(value); }
}
