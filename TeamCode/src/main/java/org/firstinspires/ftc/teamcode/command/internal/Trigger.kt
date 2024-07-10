package org.firstinspires.ftc.teamcode.command.internal

import java.util.function.BooleanSupplier

class Trigger(var supplier: BooleanSupplier, var command: Command = Command()) {
    constructor(supplier: BooleanSupplier): this(supplier, Command())
    var lastValue = supplier.asBoolean
    var value = supplier.asBoolean

    val triggered: Boolean
        get() = value

    fun update() {
        lastValue = value
        value = supplier.asBoolean
    }

    fun and(other: Trigger) = Trigger { supplier.asBoolean and other.supplier.asBoolean }
    fun or(other: Trigger) = Trigger { supplier.asBoolean or other.supplier.asBoolean }
    fun negate() = Trigger { !supplier.asBoolean }

    operator fun not() = negate()

    fun onTrue(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == true and this.lastValue == false },
                command
            )
        )
        return this
    }
    fun onFalse(command: Command): Trigger{
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == false and this.lastValue == true },
                command
            )
        )
        return this
    }

    fun whileTrue(command: Command): Trigger{
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == true and this.lastValue == false },
                command racesWith Command(
                    isFinished = { this.value == false }
                )
            )
        )
        return this
    }
    fun whileFalse(command: Command): Trigger{
        CommandScheduler.addTrigger(
            Trigger(
                { this.value == false and this.lastValue == true },
                command racesWith Command(
                    isFinished = { this.value == false }
                )
            )
        )
        return this
    }


}