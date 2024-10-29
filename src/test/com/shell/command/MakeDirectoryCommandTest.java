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

class MakeDirectoryCommandTest {
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
    void whenDirectoryDoesNotExistMakeDirectoryCreatesIt() {
        List<String> directories = List.of("newDir");

        Command command = new MakeDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        Path newDirPath = workingDirectory.resolve("newDir");
        assertThat(Files.exists(newDirPath)).isTrue();
        assertThat(outputStringWriter.toString()).contains("Directory created: " + newDirPath.toString());
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenNestedDirectoryDoesNotExistMakeDirectoryCreatesIt() {
        List<String> directories = List.of("nested/dir/path");

        Command command = new MakeDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        Path nestedDirPath = workingDirectory.resolve("nested/dir/path");
        assertThat(Files.exists(nestedDirPath)).isTrue();
        assertThat(outputStringWriter.toString()).contains("Directory created: " + nestedDirPath.toString());
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenDirectoryAlreadyExistsMakeDirectoryShowsError() throws IOException {
        Files.createDirectory(workingDirectory.resolve("existingDir"));
        List<String> directories = List.of("existingDir");

        Command command = new MakeDirectoryCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("mkdir: existingDir Directory already exists");
    }
}
