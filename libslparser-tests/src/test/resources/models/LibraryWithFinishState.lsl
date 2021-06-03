library LibraryWithFinishStates;

types {
    CustomString (String);
}

automaton Test {
    state A, B;
    finishstate F;
    var testVariable : CustomString;
}