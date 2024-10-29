package com.shell.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class ManualCommandTest {
    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;

    @BeforeEach
    void setUp() {
        outputStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorStringWriter = new StringWriter();
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner("");
    }

    @AfterEach
    void tearDown() {
        outputWriter.close();
        errorWriter.close();
        inputScanner.close();
    }

    @Test
    void whenValidCommandNameManualCommandDisplaysManual() {
        Command command = new ManualCommand("ls");
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString().trim();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("ls - Lists directory contents");
    }

    @Test
    void whenInvalidCommandNameManualCommandShowsError() {
        Command command = new ManualCommand("nonexistent");
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("No manual entry for nonexistent");
    }

    @Test
    void whenNoCommandNameProvidedManualCommandShowsError() {
        Command command = new ManualCommand("");
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("Please specify a command to view the manual.");
    }

    @Test
    void whenCommandManIsCalledManualCommandShowsHelp() {
        Command command = new ManualCommand("man");
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString().trim();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("man - Displays the manual page for a command.");
    }
}
