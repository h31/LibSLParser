package ru.spbstu.insys.libsl.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.nio.file.Path

/**
 * Created by artyom on 13.07.17.
 */
class ModelParser {
    fun parse(stream: InputStream): LibraryDecl {
        val charStream = CharStreams.fromStream(stream)
        val lexer = LibSLLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = LibSLParser(tokenStream)
        val start = parser.start()

        return LibSLReader().visitStart(start)
    }

    fun readIncludes(start: LibSLParser.StartContext, includesDir: Path) {
        for (section in start.sections().includeSection()) {
            for (statement in section.includedStatement()) {
                val includedModelName = statement.includedName().text
                val modelPath = includesDir.resolve("$includedModelName.lsl")
            }
        }
    }
}

private class LibSLReader : LibSLBaseVisitor<Node>() {
    override fun visitStart(ctx: LibSLParser.StartContext): LibraryDecl {
        val libraryName = ctx.libslHeaderSection().libraryName().Identifier().text
        val sections = ctx.sections()
        val imports =
            sections.importSection().singleOrNull()?.importedStatement()?.map { it.importedName().text } ?: listOf()
        val includes =
            sections.includeSection().singleOrNull()?.includedStatement()?.map { it.includedName().text } ?: listOf()
        val automata = sections.automatonDescription().map { visitAutomatonDescription(it) }
        val typeList = sections.typesSection().single().typeDecl().map { visitTypeDecl(it) }
        val converters =
            sections.convertersSection().singleOrNull()?.converter()?.map { visitConverter(it) } ?: listOf()
        val functions = sections.funDecl().map { visitFunDecl(it) }
        return LibraryDecl(
            name = libraryName, imports = imports, includes = includes, automata = automata, types = typeList,
            converters = converters, functions = functions
        )
    }

    override fun visitAutomatonDescription(ctx: LibSLParser.AutomatonDescriptionContext): Automaton {
        val states = ctx.stateDecl().flatMap { visitStateDecl(it) }
        val shifts = ctx.shiftDecl().map { visitShiftDecl(it) }
        val extendable = ctx.extendableFlag().any()
        return Automaton(
            name = visitSemanticType(ctx.automatonName().semanticType()),
            states = states,
            shifts = shifts,
            extendable = extendable
        )
    }

    override fun visitTypeDecl(ctx: LibSLParser.TypeDeclContext): TypeDecl =
        TypeDecl(semanticType = visitSemanticType(ctx.semanticType()), codeType = CodeType(ctx.codeType().text))

    override fun visitStateDecl(ctx: LibSLParser.StateDeclContext): NodeList<StateDecl> =
        NodeList(ctx.stateName().map { StateDecl(name = it.text) })

    override fun visitShiftDecl(ctx: LibSLParser.ShiftDeclContext): ShiftDecl =
        ShiftDecl(from = ctx.srcState().text, to = ctx.dstState().text,
            functions = ctx.funName().map { it.text })

    override fun visitConverter(ctx: LibSLParser.ConverterContext): Converter =
        Converter(
            entity = visitSemanticType(ctx.destEntity().semanticType()),
            expression = ctx.converterExpression().text
        )

    override fun visitFunDecl(ctx: LibSLParser.FunDeclContext): FunctionDecl {
        val args = ctx.funArgs()?.funArg()?.map { visitFunArg(it) } ?: listOf()
        val actions = ctx.funProperties().map { visit(it) }.filterIsInstance<ActionDecl>()
        val staticName = ctx.funProperties().map { visit(it) }.filterIsInstance<StaticDecl>().singleOrNull()
        val properties = ctx.funProperties().map { visit(it) }.filterIsInstance<PropertyDecl>()
        return FunctionDecl(
            entity = findFunctionEntity(ctx, args),
            name = ctx.funName().text,
            args = args, actions = actions,
            returnValue = visitFunReturnType(ctx.funReturnType()),
            staticName = staticName,
            properties = properties
        )
    }

    override fun visitActionDecl(ctx: LibSLParser.ActionDeclContext): Node {
        return ActionDecl(name = ctx.actionName().text, args = ctx.Identifier().map { it.text })
    }

    override fun visitFunArg(ctx: LibSLParser.FunArgContext): FunctionArgument =
        FunctionArgument(
            name = ctx.argName().text,
            type = visitSemanticType(ctx.argType().semanticType()),
            annotations = ctx.annotation().map { it.annotationName().text }
        )

    override fun visitStaticDecl(ctx: LibSLParser.StaticDeclContext): StaticDecl =
        StaticDecl(staticName = ctx.staticName()?.text ?: "")

    override fun visitPropertyDecl(ctx: LibSLParser.PropertyDeclContext): PropertyDecl =
        PropertyDecl(key = ctx.propertyKey().text, value = ctx.propertyValue().text)

    private fun findFunctionEntity(func: LibSLParser.FunDeclContext, args: List<FunctionArgument>): FunctionEntityDecl {
        val explicitEntity = func.entityName()
        return if (explicitEntity != null) {
            FunctionEntityDecl(
                type = visitSemanticType(explicitEntity.semanticType()),
                FunctionEntityDecl.FunctionEntityDeclStyle.EXPLICIT_BEFORE_NAME
            )
        } else {
            FunctionEntityDecl(
                type = args.singleOrNull { it.annotations.contains("handle") }?.type
                    ?: error("No @handle argument in function ${func.text}"),
                FunctionEntityDecl.FunctionEntityDeclStyle.VIA_HANDLE_ANNOTATION
            )
        }
    }

    override fun visitFunReturnType(ctx: LibSLParser.FunReturnTypeContext?): ReturnTypeDecl? = ctx?.run {
        ReturnTypeDecl(
            type = visitSemanticType(semanticType()),
            annotations = annotation().map { it.annotationName().text }
        )
    }

    override fun visitSemanticType(ctx: LibSLParser.SemanticTypeContext): SemanticType =
        when {
            ctx.semanticType().size == 2 -> ComplexSemanticType(
                ctx.text, // TODO: Don't use text here
                enclosingType = visitSemanticType(ctx.semanticType(0)),
                innerType = visitSemanticType(ctx.semanticType(1))
            )
            ctx.semanticType().size == 1 && ctx.arrayIdentifier() != null ->
                ComplexSemanticType(
                    enclosingType = SimpleSemanticType("[]"),
                    innerType = visitSemanticType(ctx.semanticType(0)),
                    typeName = "${ctx.semanticType(0).text}[]"
                )
            ctx.semanticType().size == 1 && ctx.pointerIdentifier() != null ->
                ComplexSemanticType(
                    enclosingType = SimpleSemanticType("*"),
                    innerType = visitSemanticType(ctx.semanticType(0)),
                    typeName = "${ctx.semanticType(0).text}*"
                )
            else -> SimpleSemanticType(ctx.text)
        }
}
