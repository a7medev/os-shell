package com.shell.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class PrintWorkingDirectoryCommandTest {
    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;
    String workingDirectory;

    @BeforeEach
    void setUp() {
        workingDirectory = "C:/path/dir/subdir";

        outputStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorStringWriter = new StringWriter();
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner("");
    }

    @Test
    void whenExecutedPwdCommandOutputsWorkingDirectory() {
        Command command = new PrintWorkingDirectoryCommand(workingDirectory);

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(outputStringWriter.toString().trim()).isEqualTo(workingDirectory);  // Verify correct output
    }
}
