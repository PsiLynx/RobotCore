package test.FakeHardware

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test

class GamepadTest: TestClass() {
    @Test
    fun testGamepadPress() {
        val gamepad = hardwareMap.get(Gamepad::class.java, "gamepad1")

        val fakeGamepad = (gamepad as FakeGamepad)

        fakeGamepad.press( "a" )
        assert(gamepad.a == true)

        fakeGamepad.depress( "a" )
        assert(gamepad.a == false)
    }
}