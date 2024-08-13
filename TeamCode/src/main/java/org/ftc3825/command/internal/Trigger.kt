package org.ftc3825.command.internal

import java.util.function.BooleanSupplier

class Trigger(var supplier: BooleanSupplier, var command: Command = Command()) {
    constructor(supplier: BooleanSupplier) : this(supplier, Command())

    private var lastValue = supplier.asBoolean
    private var value = supplier.asBoolean

    val isTriggered: Boolean
        get() = value

    fun update() {
        lastValue = value
        value = supplier.asBoolean
    }

    infix fun and(other: Trigger) = Trigger {  isTriggered and other.isTriggered }
    infix fun or(other: Trigger)  = Trigger {  isTriggered or  other.isTriggered }
    operator fun not()            = Trigger { !isTriggered }

    fun onTrue(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == true and this.lastValue == false },
                command
            )
        )
        return this
    }

    fun onFalse(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == false and this.lastValue == true },
                command
            )
        )
        return this
    }

    fun whileTrue(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == true and this.lastValue == false },
                command racesWith WaitUntilCommand { this.value == false }

            )
        )
        return this
    }

    fun whileFalse(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == false and this.lastValue == true },
                command racesWith WaitUntilCommand { this.value == true }
            )
        )
        return this
    }


}