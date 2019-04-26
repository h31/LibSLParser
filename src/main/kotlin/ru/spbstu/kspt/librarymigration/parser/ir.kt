package ru.spbstu.kspt.librarymigration.parser

val primitiveSemanticTypes = listOf("String", "Int", "Char", "Boolean")

fun LibraryDecl.getArrayTypesFromFunctionDecls(): Collection<SemanticType> =
        this.functions.flatMap { it.args }.map { it.type }.filter { it.isArray() }.toSet()

fun SemanticType.itemType() = SemanticType(typeName.removeSuffix("[]"))

fun SemanticType.isArray() = typeName.endsWith("[]")

fun SemanticType.isReference() = typeName !in primitiveSemanticTypes

//fun Type.arrayType() = "$typeName[]"

operator fun List<TypeDecl>.get(semanticType: SemanticType) = first { it.semanticType == semanticType }

fun LibraryDecl.addArrayTypeDecls(convertToCodeArrayType: (CodeType) -> CodeType): LibraryDecl {
    val arrayTypes = this.getArrayTypesFromFunctionDecls()
    val typeDecls = arrayTypes.map { arrayType ->
        val itemType = arrayType.itemType()
        val codeItemType = types[itemType].codeType
        val codeArrayType = convertToCodeArrayType(codeItemType)
        TypeDecl(semanticType = arrayType, codeType = codeArrayType)
    }
    return this.copy(types = this.types + typeDecls)
}

fun LibraryDecl.generateHandlersForArrayTypes(): LibraryDecl {
    val arrayTypes = this.types.filter { it.semanticType.typeName.endsWith("[]") }
//    val automata = mutableListOf<Automaton>()
    val newFunctionDecl = mutableListOf<FunctionDecl>()
    for (arrayType in arrayTypes) {
        val itemType = arrayType.semanticType.itemType()// getItemType(type, types)
        val codeType = arrayType.codeType //getArrayType(itemType)
        val baseFunctionDecl = FunctionDecl(entity = arrayType.semanticType, name = "", args = listOf(),
                actions = listOf(), returnValue = null, staticName = null,
                properties = listOf(), builtin = true)
        val set = baseFunctionDecl.copy(name = "set<$itemType>")
        val get = baseFunctionDecl.copy(name = "get<$itemType>")
        val memAlloc = baseFunctionDecl.copy(name = "mem_alloc<$itemType>")
        newFunctionDecl += listOf(set, get, memAlloc)
//        val arrayAutomaton = Automaton(name = type,
//                states = listOf(StateDecl("Created"), StateDecl("Constructed")),
//                shifts = listOf(), extendable = false)
//        automata += arrayAutomaton
    }
    return copy(functions = functions + newFunctionDecl)
}

val defaultStates = listOf(StateDecl("Created"), StateDecl("Constructed"), StateDecl("Closed"))

fun LibraryDecl.addDefaultStates(): LibraryDecl = copy(automata = automata.map { it.copy(states = (it.states + defaultStates).distinct()) })

fun LibraryDecl.addMissingAutomata(): LibraryDecl {
    val existingAutomataNames = automata.map { it.name }
    val generatedAutomata = functions
            .filter { it.entity !in existingAutomataNames }
            .map { Automaton(name = it.entity, states = defaultStates, shifts = listOf(), extendable = false) }
    return this.copy(automata = automata + generatedAutomata)
}
