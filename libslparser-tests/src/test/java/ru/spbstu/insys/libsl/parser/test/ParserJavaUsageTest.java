package ru.spbstu.insys.libsl.parser.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.spbstu.insys.libsl.parser.LibraryDecl;
import ru.spbstu.insys.libsl.parser.ModelParser;
import ru.spbstu.insys.libsl.parser.PrettyPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ParserJavaUsageTest {
    private InputStream readResourceAsString(String name) {
        return ParserJavaUsageTest.class.getResourceAsStream(name);
    }

    @Test
    public void z3test() throws IOException {
        LibraryDecl parsedModel;
        try (InputStream sourceModel = readResourceAsString("/models/Z3.lsl")) {
            assertNotNull(sourceModel);
            parsedModel = new ModelParser().parse(sourceModel);
        }
        String text = PrettyPrinter.print(parsedModel);
        assertEquals("library Z3;", text.substring(0, text.indexOf('\n')));
        Assertions.assertTrue(parsedModel.getFunctions().stream().anyMatch(func -> func.getName().equals("Z3_mk_bool_sort")));
        InputStreamReader modelStreamReader = new InputStreamReader(readResourceAsString("/models/Z3.lsl"));
        try (BufferedReader reader = new BufferedReader(modelStreamReader)) {
            String expected = reader.lines().collect(Collectors.joining("\n", "", "\n"));
            assertEquals(expected, text);
        }
    }
}
