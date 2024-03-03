package org.firstinspires.ftc.teamcode.component;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class YellowJacket312 extends Motor{
    public YellowJacket312(String name, HardwareMap hardwareMap){
        super(name, hardwareMap);
        setRpm(312);
        setTicksPerRev(537.7);
    }
}
