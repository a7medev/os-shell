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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class TouchCommandTest {
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
    void whenParentDirectoryExistsGivenFilePathsTouchCreatesFiles() {
        List<String> files = Arrays.asList("file1.txt", "index.html", "style.css");

        Command command = new TouchCommand(files, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(files).allSatisfy((file) -> assertThat(Files.exists(workingDirectory.resolve(file))).isTrue());
    }

    @Test
    void whenParentDirectoryDoesNotExistGivenFilePathsTouchCreatesFilesExceptNonExistentOnes() {
        List<String> validFiles = Arrays.asList("file1.txt", "style.css");
        List<String> invalidFiles = List.of("pages/index.html", "scripts/util/main.js");
        List<String> files = new ArrayList<>(invalidFiles);
        files.addAll(validFiles);

        Command command = new TouchCommand(files, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(validFiles).allSatisfy((file) -> assertThat(Files.exists(workingDirectory.resolve(file))).isTrue());
        assertThat(invalidFiles).allSatisfy((file) -> {
            assertThat(Files.exists(workingDirectory.resolve(file))).isFalse();
            assertThat(errorStringWriter.toString()).contains(file + ": Failed to create file");
        });
    }
}
