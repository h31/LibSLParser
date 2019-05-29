package ru.spbstu.kspt.librarymigration.parser

import org.stringtemplate.v4.STGroupFile

fun LibraryDecl.print(): String {
    val group = STGroupFile("prettyprinter/librarydecl.stg")
    val st = group.getInstanceOf("libraryDecl")
    st.add("decl", this.associateAutomataWithFuncs())
    return st.render()
}

//class LibraryDeclPrettyPrinter(val decl: LibraryDecl) {
//    private fun printLibraryDecl(): String = "library ${decl.name} { \n" +
//            printListOfImports(decl.imports) +
//            printListOfAutomaton(decl.automata) +
//            " }"
//
//    private fun printListOfImports(list: List<String>): String = if (list.isNotEmpty()) " imports { \n" +
//            list.joinToString(separator = ";\n  ", prefix = "  ", postfix = ";\n") +
//            " }\n" else ""
//
//    private fun printListOfAutomaton(list: List<Automaton>): String = list.joinToString(separator = "\n\n", transform = { printAutomaton(it) })
//
//    private fun printAutomaton(automaton: Automaton): String = "automaton ${automaton.name} {" +
//            " ${printListOfStateDecl(automaton.states)}" +
//            " ${printListOfShiftDecl(automaton.shifts)} }"
//
//    private fun printListOfStateDecl(list: List<StateDecl>): String = "state ${list.joinToString(transform = { it.name })}; \n"
//
//    private fun printListOfShiftDecl(list: List<ShiftDecl>): String = list.joinToString(separator = "\n\n", transform = { printShiftDecl(it) })
//
//    private fun printShiftDecl(decl: ShiftDecl): String = "shift ${decl.from} -> ${decl.to} (${decl.functions.joinToString()})"
//
//    override fun toString() = printLibraryDecl()
//}