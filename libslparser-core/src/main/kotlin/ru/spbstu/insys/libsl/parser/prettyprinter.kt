package ru.spbstu.insys.libsl.parser

import org.stringtemplate.v4.STGroupFile

fun LibraryDecl.print(): String {
    val group = STGroupFile("prettyprinter/librarydecl.stg")
    val st = group.getInstanceOf("libraryDecl")
    st.add("decl", this.associateAutomataWithFunctions())
    return st.render()
}

/**
 * For Java compatibility
 */
object PrettyPrinter {
    @JvmStatic
    fun print(library: LibraryDecl) = library.print()
}