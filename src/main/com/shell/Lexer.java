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
        while(!isAtEnd()) {
            start = current;
            getToken();
        }
        return tokens;
    }

    private void getToken() {
        char c = advance();

        switch (c) {
            case '>':
                addToken(TokenType.OPERATOR ,match('>') ? ">>" :">");
                break;
            case '<':
                addToken(TokenType.OPERATOR ,match('<') ? "<<" :"<");
                break;
            case '|':
                addToken(TokenType.OPERATOR ,"|");
                isNextCommand = true;
                break;
            case '\\':
                while(peek() != ' ' && !isAtEnd()) {
                    advance();
                }
                String escapedChars = source.substring(start + 1, current - 1);
                advance();
                addToken(TokenType.PARAMETER ,escapedChars);
                break;
            case ' ':
                break;
            default:
                while(peek() != ' ' && !isAtEnd()) {
                    advance();
                }
                String text = source.substring(start, current - 1);
                advance();
                if(isNextCommand) {
                    addToken(TokenType.COMMAND, text);
                    isNextCommand = false;
                } else if(text.charAt(0) == '-') {
                    addToken(TokenType.FLAG, text);
                } else {
                    addToken(TokenType.PARAMETER, text);
                }
        }
    }

    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expectedChar) {
        if(isAtEnd()) {
            return false;
        }
        if(expectedChar != source.charAt(current)) {
           return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if(isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
