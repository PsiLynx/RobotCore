package org.firstinspires.ftc.teamcode.subsystem.transfer

import org.firstinspires.ftc.teamcode.component.Component

class FakeIo : Io {
    var _power = 0.0
    var _blockerPos = Component.Opening.CLOSED
    override fun setPower(pow: Double) {
        _power = pow
    }

    override fun setBlockerPos(pos: Component.Opening) {
        _blockerPos = pos
    }

    override fun update(deltaTime: Double) { }

    override fun reset() { }

}