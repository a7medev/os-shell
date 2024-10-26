package com.shell.command;

import com.shell.util.FileUtils;
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

import static org.assertj.core.api.Assertions.*;

class MoveCommandTest {
    Path workingDirectory;
    Path file1;
    Path file2;
    Path directory1;
    Path directory2;

    StringWriter outputStringWriter;
    StringWriter errorStringWriter;
    PrintWriter outputWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;

    @BeforeEach
    void setUp() throws IOException {
        workingDirectory = Files.createTempDirectory("working-directory");

        file1 = Files.createTempFile(workingDirectory, "file1", ".txt");
        Files.writeString(file1, "File 1 contents");

        file2 = Files.createTempFile(workingDirectory, "file2", ".txt");
        Files.writeString(file2, "File 2 contents");

        directory1 = Files.createTempDirectory(workingDirectory, "directory1");
        directory2 = Files.createTempDirectory(workingDirectory, "directory2");

        outputStringWriter = new StringWriter();
        errorStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner(System.in);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(workingDirectory);
        outputWriter.close();
        errorWriter.close();
    }

    @Test
    void givenTwoFilesWhenTargetExistsThenOverwritesSecondFile() throws IOException {
        Command command = new MoveCommand(
                List.of(file1.toString(), file2.toString()),
                true, // force overwrite
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(Files.exists(file1)).isFalse();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 1 contents");
    }

    @Test
    void givenTwoDirectoriesWhenTargetExistsThenMovesDirectoryIntoTarget() {
        Path nestedDir = directory2.resolve(directory1.getFileName());

        Command command = new MoveCommand(
                List.of(directory1.toString(), directory2.toString()),
                true,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(Files.exists(nestedDir)).isTrue();
        assertThat(Files.exists(directory1)).isFalse();
    }

    @Test
    void givenTwoDirectoriesWhenTargetDoesNotExistThenRenamesDirectory() throws IOException {
        Path newDir = workingDirectory.resolve("newDirectory");

        Command command = new MoveCommand(
                List.of(directory1.toString(), newDir.toString()),
                true,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(Files.exists(newDir)).isTrue();
        assertThat(Files.exists(directory1)).isFalse();
    }

    @Test
    void givenMultipleFilesAndDirectoryWhenTargetIsDirectoryThenMovesAllIntoDirectory() throws IOException {
        Path targetDir = workingDirectory.resolve("newDir");
        Files.createDirectory(targetDir);

        Command command = new MoveCommand(
                List.of(file1.toString(), file2.toString(), targetDir.toString()),
                true,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(Files.exists(targetDir.resolve(file1.getFileName()))).isTrue();
        assertThat(Files.exists(targetDir.resolve(file2.getFileName()))).isTrue();
        assertThat(Files.exists(file1)).isFalse();
        assertThat(Files.exists(file2)).isFalse();
    }

    @Test
    void givenMultipleFilesAndNonDirectoryTargetThenOutputsError() {
        Command command = new MoveCommand(
                List.of(file1.toString(), file2.toString(), file1.toString()), // file1 is not a directory
                true,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(errorStringWriter.toString()).contains("mv: " + file1 + " is not a directory");
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
    }

    @Test
    void givenInsufficientArgumentsThenOutputsUsage() {
        Command command = new MoveCommand(
                List.of(file1.toString()),
                true,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(errorStringWriter.toString()).contains("usage: mv [-f] source target");
    }

    @Test
    void whenMovingFileWithoutForceAndUserConfirmsOverwriteThenOverwritesFile() throws IOException {
        inputScanner = new Scanner("y\n"); // User inputs "y" to confirm overwrite
        Command command = new MoveCommand(
                List.of(file1.toString(), file2.toString()),
                false,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(outputStringWriter.toString()).contains(file2.toString() + " already exists, do you want to overwrite it? (y/N)");
        assertThat(Files.exists(file1)).isFalse();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 1 contents");
    }

    @Test
    void whenMovingFileWithoutForceAndUserDeclinesOverwriteThenDoesNotOverwriteFile() throws IOException {
        inputScanner = new Scanner("n\n"); // User inputs "n" to decline overwrite
        Command command = new MoveCommand(
                List.of(file1.toString(), file2.toString()),
                false,
                workingDirectory.toString(),
                outputWriter,
                errorWriter,
                inputScanner);

        command.execute();

        assertThat(outputStringWriter.toString()).contains(file2.toString() + " already exists, do you want to overwrite it? (y/N)");
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 2 contents");
    }
}
