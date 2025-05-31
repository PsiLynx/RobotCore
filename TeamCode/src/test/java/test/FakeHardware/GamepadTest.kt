package test.FakeHardware

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test

class GamepadTest: TestClass() {
    @Test
    fun testGamepadPress() {
        val gamepad = FakeGamepad()

        gamepad.press( "a" )
        assert(gamepad.a == true)

        gamepad.depress( "a" )
        assert(gamepad.a == false)
    }
}