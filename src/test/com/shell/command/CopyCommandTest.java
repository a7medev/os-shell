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

import static org.assertj.core.api.Assertions.assertThat;

class CopyCommandTest {
    Path workingDirectory;
    Path file1;
    Path file2;

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
        inputScanner.close();
    }

    @Test
    void givenTwoFilesWhenTargetExistsThenOverwritesSecondFile() throws IOException {
        Command command = new CopyCommand(
                List.of(file1.toString(), file2.toString()),
                true, // force overwrite
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 1 contents");
    }

    @Test
    void givenMultipleFilesAndDirectoryWhenTargetIsDirectoryThenCopiesAllIntoDirectory() throws IOException {
        Path targetDir = workingDirectory.resolve("newDir");
        Files.createDirectory(targetDir);

        Command command = new CopyCommand(
                List.of(file1.toString(), file2.toString(), targetDir.toString()),
                true,
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(targetDir.resolve(file1.getFileName()))).isTrue();
        assertThat(Files.exists(targetDir.resolve(file2.getFileName()))).isTrue();
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
    }

    @Test
    void givenMultipleFilesAndNonDirectoryTargetThenOutputsError() {
        Command command = new CopyCommand(
                List.of(file1.toString(), file2.toString(), file1.toString()), // file1 is not a directory
                true,
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("cp: " + file1 + ": is not a directory");
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
    }

    @Test
    void givenInsufficientArgumentsThenOutputsUsage() {
        Command command = new CopyCommand(
                List.of(file1.toString()),
                true,
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("usage: cp [-f] source target");
    }

    @Test
    void whenCopyingFileWithoutForceAndUserConfirmsOverwriteThenOverwritesFile() throws IOException {
        inputScanner = new Scanner("y\n"); // User inputs "y" to confirm overwrite
        Command command = new CopyCommand(
                List.of(file1.toString(), file2.toString()),
                false,
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).contains(file2.toString() + " already exists, do you want to overwrite it? (y/N)");
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 1 contents");
    }

    @Test
    void whenCopyingFileWithoutForceAndUserDeclinesOverwriteThenDoesNotOverwriteFile() throws IOException {
        inputScanner = new Scanner("n\n"); // User inputs "n" to decline overwrite
        Command command = new CopyCommand(
                List.of(file1.toString(), file2.toString()),
                false,
                workingDirectory.toString());

        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).contains(file2.toString() + " already exists, do you want to overwrite it? (y/N)");
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo("File 2 contents");
    }
}
