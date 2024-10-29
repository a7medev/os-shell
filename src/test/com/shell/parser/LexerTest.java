package com.shell.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void givenAnEchoCommandThenLexerReturnsCorrectTokens() {
        String SOURCE = "echo Hello World";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "echo");
        Token para1 = new Token(TokenType.PARAMETER, "Hello");
        Token para2 = new Token(TokenType.PARAMETER, "World");

        List<Token> expected = List.of(command, para1, para2);

        assertThat(tokens).isEqualTo(expected);
    }

    @Test
    void givenSingleHyphenFlagThenLexerReturnCorrectTokens() {
        String SOURCE = "rm -rf dir";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "rm");
        Token para1 = new Token(TokenType.FLAG, "r");
        Token para2 = new Token(TokenType.FLAG, "f");
        Token para3 = new Token(TokenType.PARAMETER, "dir");

        List<Token> expected = List.of(command, para1, para2, para3);

        assertThat(tokens).isEqualTo(expected);
    }

    @Test
    void givenDoubleHyphenFlagThenLexerReturnCorrectTokens() {
        String SOURCE = "ls --all";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "ls");
        Token para1 = new Token(TokenType.FLAG, "all");

        List<Token> expected = List.of(command, para1);

        assertThat(tokens).isEqualTo(expected);
    }

    @Test
    void givenSingleQuotesInTheLineThenLexerReturnCorrectTokens() {
        String SOURCE = "echo 'Hello, World!!'";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "echo");
        Token para1 = new Token(TokenType.PARAMETER, "Hello, World!!");

        List<Token> expected = List.of(command, para1);

        assertThat(tokens).isEqualTo(expected);
    }

    @Test
    void givenDoubleQuotesInTheLineThenLexerReturnCorrectTokens() {
        String SOURCE = "echo \"Hello, World!!\"";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "echo");
        Token para1 = new Token(TokenType.PARAMETER, "Hello, World!!");

        List<Token> expected = List.of(command, para1);

        assertThat(tokens).isEqualTo(expected);
    }

    @Test
    void givenPipeOperatorThenTheLexerReturnCorrectTokens() {
        String SOURCE = "sort file3 | uniq | grep 3";
        Lexer lexer = new Lexer(SOURCE);

        List<Token> tokens = lexer.getTokens();

        Token command = new Token(TokenType.COMMAND, "sort");
        Token para1 = new Token(TokenType.PARAMETER, "file3");
        Token pipe1 = new Token(TokenType.OPERATOR, "|");
        Token command1 = new Token(TokenType.COMMAND, "uniq");
        Token pipe2 = new Token(TokenType.OPERATOR, "|");
        Token command3 = new Token(TokenType.COMMAND, "grep");
        Token para2 = new Token(TokenType.PARAMETER, "3");

        List<Token> expected = List.of(command, para1, pipe1, command1, pipe2, command3, para2);

        assertThat(tokens).isEqualTo(expected);
    }
}