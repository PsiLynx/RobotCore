package org.firstinspires.ftc.teamcode.component

import com.sun.tools.javac.util.Assert
import org.firstinspires.ftc.teamcode.fakehardware.fakeMotor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EncoderTest {
    var encoder = Encoder(fakeMotor(), 8192.0, 1.0, 1.0)

    @Test
    fun test(){
        assertTrue(2 == 2)
    }
}