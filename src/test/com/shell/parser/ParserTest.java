package com.shell.parser;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class ParserTest {

    @Test
    void givenCommandWithoutPipesThenParserReturnsCorrectExpression() throws UnexpectedTokenException {
        String src = "echo hello, world, hello, world > file1";
        Lexer lexer = new Lexer(src);

        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);

        Expression expr = parser.parse();

        List<String> args = List.of("hello,", "world,", "hello,", "world");
        List<String> flags = new ArrayList<>();

        Expression redirection = new Expression.OutputRedirection("file1");
        Expression expected = new Expression.Command("echo", args, flags, redirection);

        assertThat(expr).isEqualTo(expected);
    }

    @Test
    void givenCommandWithPipesThenParserReturnsCorrectExpression() throws UnexpectedTokenException {
        String src = "cat file | sort | grep mahmoud";
        Lexer lexer = new Lexer(src);

        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);

        Expression expr = parser.parse();

        List<String> args = List.of("file");
        List<String> flags = new ArrayList<>();

        List<String> args1 = List.of("mahmoud");

        Expression expected = new Expression.PipedCommands(
                new Expression.Command("cat", args, flags, null),
                new Expression.PipedCommands(
                        new Expression.Command("sort", new ArrayList<>(), new ArrayList<>(), null),
                        new Expression.Command("grep", args1, new ArrayList<>(), null)
                )
        );

        assertThat(expr).isEqualTo(expected);
    }

    @Test
    void givenSingleCommandThenParserReturnsCorrectExpression() throws UnexpectedTokenException {
        String src = "ls";
        Lexer lexer = new Lexer(src);

        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(tokens);

        Expression expr = parser.parse();

        List<String> args = new ArrayList<>();
        List<String> flags = new ArrayList<>();

        Expression expected = new Expression.Command("ls", args, flags, null);

        assertThat(expr).isEqualTo(expected);
    }
}