library LibraryWithVariables;

types {
    CustomString (String);
}

automaton Test {
    state A;
    var testVariable : CustomString;
}

fun Test.foo() {
    testVariable = new Test(A);
}