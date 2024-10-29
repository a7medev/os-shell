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

class RemoveDirectoryCommandTest {
    Path workingDirectory;
    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;

    @BeforeEach
    void setUp() throws IOException {
        workingDirectory = Files.createTempDirectory("working-directory");

        outputStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorStringWriter = new StringWriter();
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner("");
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(workingDirectory);
        outputWriter.close();
        errorWriter.close();
        inputScanner.close();
    }

    @Test
    void whenDirectoryIsEmptyRemoveDirectoryRemovesIt() throws IOException {
        List<String> directories = List.of("emptyDir");
        Path emptyDirPath = workingDirectory.resolve("emptyDir");
        Files.createDirectory(emptyDirPath);

        Command command = new RemoveDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(emptyDirPath)).isFalse();
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenDirectoryIsNotEmptyRemoveDirectoryShowsError() throws IOException {
        List<String> directories = List.of("nonEmptyDir");
        Path nonEmptyDirPath = workingDirectory.resolve("nonEmptyDir");
        Files.createDirectory(nonEmptyDirPath);
        Files.createFile(nonEmptyDirPath.resolve("file.txt"));

        Command command = new RemoveDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(nonEmptyDirPath)).isTrue();
        assertThat(errorStringWriter.toString()).contains("rmdir: nonEmptyDir Directory is not empty");
    }

    @Test
    void whenDirectoryDoesNotExistRemoveDirectoryShowsError() {
        List<String> directories = List.of("nonExistentDir");

        Command command = new RemoveDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).contains("rmdir: nonExistentDir Directory does not exist");
    }

    @Test
    void whenPathIsNotADirectoryRemoveDirectoryShowsError() throws IOException {
        List<String> directories = List.of("notADirectory");
        Path notADirectoryPath = workingDirectory.resolve("notADirectory");
        Files.createFile(notADirectoryPath);

        Command command = new RemoveDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(Files.exists(notADirectoryPath)).isTrue();
        assertThat(errorStringWriter.toString()).contains("rmdir: notADirectory is not a directory");
    }
}
