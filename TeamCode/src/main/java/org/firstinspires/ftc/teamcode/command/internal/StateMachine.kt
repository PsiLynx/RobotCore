package org.firstinspires.ftc.teamcode.command.internal


// TODO: requirements

class StateMachine(name: String): Command(name = { name }), Collection<StateMachine.State> {

    private var states: MutableList<State> = mutableListOf()

    private var finished = false

    private var index = 0
    var currentState: State
        get() = states[index]
        internal set(value) {
            if (states.contains(value)) {
                index = states.indexOf(value)
            }
        }

    fun changeState(state: State?): Boolean {
        if (state == null) {
            finished = true
            return true
        } else if (states.contains(state)) {
            currentState.exit()
            currentState = state
            currentState.enter()
            return true
        } else {
            return false
        }
    }

    override fun initialize() {
        currentState.enter()
    }

    override fun execute() {
        currentState.update()
    }

    override fun end(interrupted: Boolean) {
        currentState.exit()
    }

    override fun isFinished() = finished

    fun addState(command: Command) = State(command, this).also { states.add(it) }

    class State(val command: Command, private val statemachine: StateMachine) {
        private var transitions = mutableListOf<Transition>()
        private var enterCommands = mutableListOf<Command>()
        private var exitCommands = mutableListOf<Command>()

        fun finished() = command.isFinished()

        fun enter() {
            enterCommands.forEach { it.schedule() }
            command.schedule()
        }

        fun update() {
            transitions.firstOrNull { it.isTriggered() }?.let {
                statemachine.changeState(it.state)
            }
        }

        fun exit() {
            CommandScheduler.end(command)
            exitCommands.forEach { it.schedule() }
        }

        infix fun switchTo(state: State) = Transition(state, this::finished).also { transitions.add(it) }
    }

    class Transition(val state: State, private val completed: () -> Boolean, condition: () -> Boolean = completed) {

        var isTriggered = condition
            internal set

        infix fun after(cond: () -> Boolean) {
            isTriggered = cond
        }

        infix fun afterCompletedAnd(cond: () -> Boolean) {
            isTriggered = { cond() && completed() }
        }

        infix fun afterCompletedOr(cond: () -> Boolean) {
            isTriggered = { cond() || completed() }
        }

        fun afterCompleted() {
            isTriggered = completed
        }

    }

    // implementation of the Collection<State> whatever whatnot

    override val size: Int
        get() = states.size

    override fun contains(element: State): Boolean {
        return states.contains(element)
    }

    override fun containsAll(elements: Collection<State>): Boolean {
        return states.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return states.isEmpty()
    }

    override fun iterator(): Iterator<State> {
        return states.iterator()
    }
}