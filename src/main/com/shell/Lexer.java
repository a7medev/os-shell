package com.shell;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private boolean isNextCommand = true;

    Lexer(String source) {
        this.source = source;
    }

    List<Token> getTokens() {
        while (!isAtEnd()) {
            start = current;
            getToken();
        }
        return tokens;
    }

    private void getToken() {
        char c = advance();

        switch (c) {
            case '>':
                addToken(TokenType.OPERATOR, match('>') ? ">>" : ">");
                break;
            case '<':
                addToken(TokenType.OPERATOR, match('<') ? "<<" : "<");
                break;
            case '|':
                addToken(TokenType.OPERATOR, "|");
                isNextCommand = true;
                break;
            case '\\':
                while (peek() != ' ' && !isAtEnd()) {
                    advance();
                }
                if (isAtEnd()) {
                    addToken(TokenType.PARAMETER, "\\");
                    break;
                }
                advance();
                String escapedChars = source.substring(start + 1, current - 1);
                addToken(TokenType.PARAMETER, escapedChars);
                break;
            case '"':
                addQuotedTextToken('"');
                break;
            case '\'':
                addQuotedTextToken('\'');
                break;
            case ' ':
                break;
            default:
                while (peek() != ' ' && !isAtEnd()) {
                    advance();
                }
                String text = source.substring(start, current);
                advance();
                if (isNextCommand) {
                    addToken(TokenType.COMMAND, text);
                    isNextCommand = false;
                } else if (text.charAt(0) == '-') {
                    addFlagToken(text);
                } else {
                    addToken(TokenType.PARAMETER, text);
                }
        }
    }

    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
    }

    private void addQuotedTextToken(char quote) {
        while (peek() != quote && !isAtEnd()) {
            advance();
        }
        if (isAtEnd()) {
            System.err.println("Unterminated String");
            return;
        }
        advance();
        addToken(TokenType.PARAMETER, source.substring(start + 1, current - 1));
    }

    private void addFlagToken(String text) {
        if (text.length() > 2 && text.charAt(1) == '-') {
            addToken(TokenType.FLAG, text.substring(2));
        } else if (text.length() >= 2) {
            for (int i = 1; i < text.length(); i++) {
                addToken(TokenType.FLAG, text.charAt(i) + "");
            }
        } else {
            addToken(TokenType.PARAMETER, text);
        }
    }

    private char advance() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current++);
    }

    private boolean match(char expectedChar) {
        if (isAtEnd()) {
            return false;
        }
        if (expectedChar != source.charAt(current)) {
            return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
