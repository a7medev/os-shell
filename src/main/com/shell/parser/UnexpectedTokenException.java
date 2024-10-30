package com.shell.parser;

public class UnexpectedTokenException extends Exception {
    public UnexpectedTokenException(TokenType unexpectedToken, TokenType expectedToken) {
        super("Unexpected token " + unexpectedToken + ", expected " + expectedToken + " instead.");
    }
}
