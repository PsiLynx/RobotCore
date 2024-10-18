package test

import org.ftc3825.opmodes.Auto
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.OpModeRunner
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import org.ftc3825.util.TestClass
import org.junit.Test
import org.firstinspires.ftc.teamcode.test.subsystem.DrivetrainTest

class OpModeTest: TestClass(){
    @Test fun testAuto(){
        /*
        OpModeRunner(
            Auto(),
            assertAfterExecute = { opMode ->
                opMode as Auto
                (
                    Drivetrain.position.x isWithin 1 of 66
                    and (Drivetrain.position.y isWithin 1 of -65)
                )
            }
        ).run()
        */
    }
}
