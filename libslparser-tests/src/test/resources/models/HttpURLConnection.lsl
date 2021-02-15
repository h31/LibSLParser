library HttpURLConnection;
types {
    URLString (string);
    URL (java.net.URL);
    InputStream (java.io.InputStream);
    ContentLength (long);
    StatusCode (int);
    OutputStream (java.io.OutputStream);
    Payload (byte[]);
    StringBody (String); // TODO: Cited nowhere in the model, is it OK?
    RequestParamName (String);
    RequestParamValue (String);
    ResponseHeaderName (String);
    ResponseHeaderValue (String);
    RequestMethod (String);
    Request (HttpURLConnection); // TODO: was commented
}

automaton Request {
    state Created;
    state Connected;
    shift Created -> Connected (connect);
    shift any -> Connected (getStatusCode, getInputStream,getHeaderField, getContentLengthLong);
    shift Created -> self (setRequestProperty, setRequestMethod, getOutputStream);
}

automaton OutputStream { // TODO: Should be placed in Java stdlib description, it's not really an HTTP library element
    state Created;
    state Flushed;
    finishstate Closed;
    shift Created -> Flushed (flush);
    shift any -> Closed (close); // TODO: Is Closed -> Closed allowed?
    shift Create -> self (write);
    shift Flushed -> Create (write); // TODO: Uses the same fun twice, is it OK?
}


fun Request.connect(); // HttpURLConnection д.б. семантическим типом


fun HttpURLConnection.connect();

fun HttpURLConnection.getStatusCode(): STATUS_CODE;

fun HttpURLConnection.getInputStream(): InputStream;

fun HttpURLConnection.setRequestProperty(name: RequestHeaderName, value: RequestHeaderValue) { // TODO: Will break current migration mechanism.
    action SET_HEADER(name, value);
}

fun HttpURLConnection.setDoOutput(flag: Boolean) {
    action USE_POST();
    when; // TODO: when flag == true -> property "method" = "POST";
}

fun HttpURLConnection.setRequestMethod(method: RequestMethod) {
    // property "method" = method;
    // TODO: Won't work if method is a variable (not a literal)
}

fun HttpURLConnection.getContentLengthLong(): ContentLength;

fun HttpURLConnection.getOutputStream(): OutputStream {
    requires; // TODO: requires property "method" == "POST";
    // TODO: Requires, right?
}

fun HttpURLConnection.getHeaderField(name: ResponseHeaderName): ResponseHeaderValue;

fun OutputStream.flush();

fun OutputStream.close();

fun OutputStream.write(payload: Payload) {
    action SET_PAYLOAD(); // TODO: OutputStream is a common Java class, but SET_PAYLOAD is an HTTP-specific action
}