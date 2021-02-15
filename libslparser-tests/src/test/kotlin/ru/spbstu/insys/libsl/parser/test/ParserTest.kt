package ru.spbstu.insys.libsl.parser.test

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.spbstu.insys.libsl.parser.ModelParser
import ru.spbstu.insys.libsl.parser.print
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParserTest {
    private fun readResourceAsString(name: String) = this.javaClass.classLoader.getResourceAsStream(name)

    private fun makeDiff(expected: String, actual: String) = expected.lineSequence().zip(actual.lineSequence())
        .filter { (source, result) -> source != result }
        .map { (source, result) -> "-$source\n+$result" }
        .joinToString("\n")

    @Test
    fun z3test() {
        val sourceModel = assertNotNull(readResourceAsString("models/Z3.lsl"))
        val parsedModel = ModelParser().parse(sourceModel)
        val text = parsedModel.print()
        assertEquals("library Z3;", text.lineSequence().first())
        assertTrue(parsedModel.functions.any { it.name == "Z3_mk_bool_sort" })
        val expected = assertNotNull(readResourceAsString("models/Z3.lsl"))
            .bufferedReader()
            .use { it.readText() }
        assertEquals(expected, text, message = makeDiff(expected, text))
    }

    @ParameterizedTest
    @ValueSource(strings = ["SocketServer", "Requests", "OkHttp", "HttpURLConnection"])
    fun modelParseTest(modelName: String) {
        val sourceModel = assertNotNull(readResourceAsString("models/$modelName.lsl"))
        val parsedModel = ModelParser().parse(sourceModel)
        assertEquals(modelName, parsedModel.name)
    }
}