package ru.spbstu.kspt.librarymigration.parser.edgemodel

import ru.spbstu.kspt.librarymigration.parser.FunctionDecl
import ru.spbstu.kspt.librarymigration.parser.LibraryDecl
import ru.spbstu.kspt.librarymigration.parser.ShiftDecl

class EdgeModelConverter {
    val machines = mutableMapOf<String, StateMachine>()
    val functions = mutableMapOf<Pair<String, String>, FunctionDecl>()
//    val entities get() = machines + types

    fun convert(libraryDecl: LibraryDecl): Library {
        libraryDecl.types.map { StateMachine(name = it.semanticType.typeName) }.associateByTo(machines, StateMachine::name)
        libraryDecl.functions.associateByTo(functions, { Pair(it.entity.typeName, it.name) })
        for (fsm in libraryDecl.automata) {
            val machine = machines[fsm.name.typeName]!!
            for (state in fsm.states) {
                State(name = state.name, machine = machine)
            }
            for (shift in fsm.shifts) {
                for (func in shift.functions) {
                    encodeFunction(machine = machine,
                            functionDecl = functions[fsm.name.typeName to func]!!,
                            shift = shift)
                }
            }
        }
        for (conv in libraryDecl.converters) {
            val machine = machines[conv.entity.typeName]!!
            val args = Regex("<(w+)>").findAll(conv.expression)
                    .map { it.groupValues[1] }.map { it to machines[it]!!.getConstructedState() }.toMap()
            TemplateEdge(machine = machine, src = machine.getInitState(),
                    dst = machine.getConstructedState(), template = conv.expression, templateParams = args)
        }
        val types = libraryDecl.types.map { machines[it.semanticType.typeName]!! to it.codeType.typeName }.toMap()

        return Library(name = libraryDecl.name,
                stateMachines = machines.values.toList(), machineTypes = types)
    }

    private fun encodeFunction(machine: StateMachine, functionDecl: FunctionDecl, shift: ShiftDecl): Edge {
        val dst = if (shift.to == "self") shift.from else shift.to
        val actionParams = functionDecl.actions.flatMap { it.args }.map { ActionParam(it) }
        val edge = CallEdge(machine = machine, src = machine.stateByName(shift.from),
                dst = machine.stateByName(dst), methodName = functionDecl.name,
                param = functionDecl.args.map { EntityParam(machine = checkNotNull(machines[it.type.typeName])) } + actionParams,
                actions = functionDecl.actions.map { Action(name = it.name) },
                hasReturnValue = functionDecl.returnValue != null,
                isStatic = functionDecl.staticName != null)
        if (functionDecl.returnValue != null) {
            LinkedEdge(edge = edge, dst = machines[functionDecl.returnValue.typeName]!!.getDefaultState())
        }
        return edge
    }

    private fun StateMachine.stateByName(name: String): State {
        return states.first { it.name == name }
    }
}