package test

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.opmodes.ThreeSpecimen
import org.ftc3825.util.OpModeRunner
import org.ftc3825.util.TestClass
import org.junit.Test

class OpModeTest: TestClass(){
    @Test fun testAuto(){
        OpModeRunner(
            ThreeSpecimen(),
            assertAfterExecute = { opMode ->
                opMode as ThreeSpecimen
                true
            },
            assertEveryLoop = { _ ->
                CommandScheduler.updatesPerLoop < 7

            }
        ).run()

    }
}
