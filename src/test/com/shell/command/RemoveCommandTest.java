package com.shell.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class RemoveCommandTest {
    private Path workingDirectory;
    private Path testFile;
    private Path testDir;
    private Path nestedFile;

    private StringWriter outputStringWriter;
    private StringWriter errorStringWriter;
    private PrintWriter outputWriter;
    private PrintWriter errorWriter;
    private Scanner inputScanner;

    @BeforeEach
    void setUp() throws IOException {
        workingDirectory = Files.createTempDirectory("working-directory");
        testFile = Files.createTempFile(workingDirectory, "test-file", ".txt");
        testDir = Files.createTempDirectory(workingDirectory, "test-dir");
        nestedFile = Files.createTempFile(testDir, "nested-file", ".txt");

        outputStringWriter = new StringWriter();
        errorStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner(System.in);  // Using a Scanner for user input simulation
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(nestedFile);
        Files.deleteIfExists(testDir);
        Files.deleteIfExists(testFile);
        Files.deleteIfExists(workingDirectory);

        outputWriter.close();
        errorWriter.close();
        inputScanner.close();
    }

    @Test
    void whenFileExistsAndForceEnabledRemovesFileWithoutPrompt() {
        RemoveCommand removeCommand = new RemoveCommand(List.of(testFile.toString()),
                true,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(testFile)).isFalse();
        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenDirectoryAndAllowDirectoryEnabledRemovesDirectoryRecursively() {
        RemoveCommand removeCommand = new RemoveCommand(List.of(testDir.toString()),
                true,
                true,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(testDir)).isFalse();
        assertThat(Files.exists(nestedFile)).isFalse();
        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenDirectoryWithoutAllowDirectoryOutputsError() {
        RemoveCommand removeCommand = new RemoveCommand(List.of(testDir.toString()),
                true,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(testDir)).isTrue();
        assertThat(errorStringWriter.toString()).contains("Is a directory");
    }

    @Test
    void whenFileDoesNotExistAndForceDisabledOutputsError() {
        RemoveCommand removeCommand = new RemoveCommand(List.of("nonexistent-file.txt"),
                false,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("No such file");
    }

    @Test
    void whenFileDoesNotExistAndForceEnabledSilentlyIgnores() {
        RemoveCommand removeCommand = new RemoveCommand(List.of("nonexistent-file.txt"),
                true,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenRemovingMultipleFilesAndSomeExistRemovesOnlyExistingFiles() throws IOException {
        Path anotherFile = Files.createTempFile(workingDirectory, "another-file", ".txt");

        RemoveCommand removeCommand = new RemoveCommand(
                List.of(testFile.toString(), "nonexistent-file.txt", anotherFile.toString()),
                true,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(testFile)).isFalse();
        assertThat(Files.exists(anotherFile)).isFalse();
        assertThat(errorStringWriter.toString()).doesNotContain("No such file");
    }

    @Test
    void whenLessArgumentsProvidedOutputsUsage() {
        RemoveCommand removeCommand = new RemoveCommand(List.of(),
                true,
                false,
                workingDirectory.toString());

        removeCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("usage: rm [-f] [-r] file ...");
    }
}
