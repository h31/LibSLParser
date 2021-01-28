package ru.spbstu.insys.libsl.parser

interface Node

data class LibraryDecl(val name: String,
                       val imports: List<String>,
                       val includes: List<String>,
                       val automata: List<Automaton>,
                       val types: List<TypeDecl>,
                       val converters: List<Converter>,
                       val functions: List<FunctionDecl>) : Node

open class NodeList<T>(val list: List<T>) : Node, List<T> by list

//class TypeList(list: List<Node>) : NodeList(list)

data class Automaton(val name: SemanticType,
                     val states: List<StateDecl>,
                     val shifts: List<ShiftDecl>,
                     val extendable: Boolean,
                     val associatedFunctions: List<FunctionDecl> = listOf()) : Node

data class TypeDecl(val semanticType: SemanticType, val codeType: CodeType) : Node

interface Type {
    val typeName: String
}

interface SemanticType : Node, Type

data class SimpleSemanticType(override val typeName: String) : SemanticType {
    override fun toString() = typeName
}

data class ComplexSemanticType(override val typeName: String,
                               val enclosingType: SemanticType,
                               val innerType: SemanticType) : SemanticType {
    override fun toString() = typeName
}

data class CodeType(override val typeName: String) : Node, Type {
    override fun toString() = typeName
}

data class Converter(val entity: SemanticType, val expression: String) : Node

data class FunctionDecl(val entity: SemanticType, val name: String,
                        val args: List<FunctionArgument>,
                        val actions: List<ActionDecl>,
                        val returnValue: SemanticType?,
                        val staticName: StaticDecl?,
                        val properties: List<PropertyDecl>,
                        val builtin: Boolean = false,
                        val codeName: String = name) : Node

data class ActionDecl(val name: String, val args: List<String>) : Node

data class FunctionArgument(val name: String, val type: SemanticType) : Node

data class StateDecl(val name: String) : Node

data class ShiftDecl(val from: String, val to: String, val functions: List<String>) : Node

data class StaticDecl(val staticName: String) : Node

data class PropertyDecl(val key: String, val value: String) : Node