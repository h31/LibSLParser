library LibraryWithVariables;

types {
    CustomString (String);
}

automaton Test {
    var testVariable : CustomString;
}

fun Test.foo() {
    testVariable = new Test();
}