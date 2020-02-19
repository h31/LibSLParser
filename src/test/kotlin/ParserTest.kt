import org.junit.Test
import ru.spbstu.kspt.librarymigration.parser.ModelParser
import ru.spbstu.kspt.librarymigration.parser.print
import java.net.URL
import kotlin.test.assertFails
import kotlin.test.fail

class ParserTest {
    fun getZ3Model(): String =
            URL("https://raw.githubusercontent.com/h31/LibraryLink/master/models/library/Z3.lsl").readText()

    private fun resourceReader(name: String) = this.javaClass.classLoader.getResourceAsStream(name).bufferedReader()

//    @Test
    fun z3test() {
        val sourceText = getZ3Model()
        val parsedModel = ModelParser().parse(sourceText.byteInputStream())
        val text = parsedModel.print()
        val expected = resourceReader("prettyprinter/Z3.lsl").readText()
        if (expected != text) {
            expected.lineSequence().zip(text.lineSequence()).forEach { (source, result) -> if (source != result) println("-$source\n+$result") }
//            fail("Not equal")
        }
    }
}