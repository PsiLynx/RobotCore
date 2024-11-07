package test

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.opmodes.Auto
import org.ftc3825.opmodes.FiveSpecimen
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.OpModeRunner
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import org.ftc3825.util.TestClass
import org.junit.Test
import org.ftc3825.test.subsystem.DrivetrainTest

class OpModeTest: TestClass(){
    @Test fun testAuto(){
        OpModeRunner(
            FiveSpecimen(),
            assertAfterExecute = { opMode ->
                opMode as FiveSpecimen
                true
            },
            assertEveryLoop = { _ ->
                CommandScheduler.updatesPerLoop < 7

            }
        ).run()

    }
}
