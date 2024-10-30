package com.shell.parser;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() throws UnexpectedTokenException {
        return commandLine();
    }

    private Expression commandLine() throws UnexpectedTokenException {
        return commandSequence();
    }

    private Expression commandSequence() throws UnexpectedTokenException {
        Expression expr = command();

        Token pipe = new Token(TokenType.OPERATOR, "|");

        while (match(pipe)) {
            Expression right = commandSequence();
            expr = new Expression.PipedCommands(expr, right);
        }

        return expr;
    }

    private Expression command() throws UnexpectedTokenException {
        String commandName = consume(TokenType.COMMAND);
        List<String> arguments = new ArrayList<>();
        List<String> flags = new ArrayList<>();
        Expression redirections = null;

        Token op1 = new Token(TokenType.OPERATOR, ">");
        Token op2 = new Token(TokenType.OPERATOR, "<");
        Token op3 = new Token(TokenType.OPERATOR, ">>");

        while (match(TokenType.FLAG)) {
            flags.add(previous().value);
        }

        while (match(TokenType.PARAMETER)) {
            arguments.add(previous().value);
        }

        while (match(op1, op2, op3)) {
            Token operator = previous();
            String file = consume(TokenType.PARAMETER);

            redirections = switch (operator.value) {
                case ">" -> new Expression.OutputRedirection(file);
                case "<" -> new Expression.InputRedirection(file);
                default -> new Expression.AppendOutputRedirection(file);
            };
        }

        return new Expression.Command(commandName, arguments, flags, redirections);
    }

    private boolean match(Token... tokens) {
        for (Token token : tokens) {
            if (check(token)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean match(TokenType tokenType) {
        if (check(tokenType)) {
            advance();
            return true;
        }

        return false;
    }

    private boolean check(Token token) {
        if (isAtEnd()) {
            return false;
        }
        return peek().equals(token);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == tokenType;
    }

    private String consume(TokenType tokenType) throws UnexpectedTokenException {
        if (!check(tokenType)) {
            throw new UnexpectedTokenException(peek().type, tokenType);
        }

        return advance().value;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
}
