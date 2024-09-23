package test

import org.ftc3825.opmodes.Auto
import org.ftc3825.util.OpModeRunner
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import org.junit.Test

class OpModeTest {
    @Test fun testAuto() = OpModeRunner(
        Auto(),
        assertAfterExecute = { opMode ->
            opMode as Auto
            (
                opMode.localizer.position.x isWithin 0.5 of 66
                 and (opMode.localizer.position.y isWithin 0.5 of -65)
            )
        }
    ).run()
}