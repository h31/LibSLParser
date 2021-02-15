library SocketServer;
imports {
    socketserver;
}

types {
    BaseRequestHandler (socketserver.BaseRequestHandler);
    Request (socketserver.Request);
    TCPServer (socketserver.TCPServer);
    Size (int);
}

converters {
    Content <- <Response>.content();
    Headers <- <Response>.headers();
}

automaton BaseRequestHandler {
    extendable;
    state Constructed;
    shift Constructed -> self (request, handle);
}

automaton Request {
    state Constructed;
    shift Constructed -> self (recv);
}

automaton TCPServer {
    state Created, Constructed;
    shift Created -> Constructed (init);
    shift Constructed -> self (serve_forever);
}

fun BaseRequestHandler.request(): Request;

fun BaseRequestHandler.handle(); // TODO: Use extendable flag

fun Request.recv(size: Size): Bytes;

fun TCPServer.serve_forever();