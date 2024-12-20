package com.shell.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.*;

public class EchoCommandTest {
    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;

    @BeforeEach
    void setUp() throws IOException {
        outputStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorStringWriter = new StringWriter();
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner(System.in);
    }

    @AfterEach
    void tearDown() throws IOException {
        outputWriter.close();
        errorWriter.close();
        inputScanner.close();
    }

    @Test
    void whenGivenArgument_printsIt() {
        List<String> arguments = List.of("hello world");

        Command test = new EchoCommand(arguments);
        test.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).contains("hello world");
        assertThat(errorStringWriter.toString()).isEmpty();
    }
}