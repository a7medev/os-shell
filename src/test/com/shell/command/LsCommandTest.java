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

class LsCommandTest {
    Path workingDirectory;
    Path subdirectory;

    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;

    @BeforeEach
    void setUp() throws IOException {
        workingDirectory = Files.createTempDirectory("working-directory");
        subdirectory = Files.createDirectory(workingDirectory.resolve("subdir"));

        // Create sample files
        Files.createFile(workingDirectory.resolve("file1.txt"));
        Files.createFile(workingDirectory.resolve("file2.txt"));
        Files.createFile(subdirectory.resolve("file3.txt"));

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
    void whenDirectoryExistsLsListsFiles() {
        List<String> directories = List.of(workingDirectory.toString());

        Command command = new LsCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("file1.txt", "file2.txt", "subdir");
    }

    @Test
    void whenSubdirectorySpecifiedLsListsFilesInSubdirectory() {
        List<String> directories = List.of(subdirectory.toString());

        Command command = new LsCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("file3.txt");
        assertThat(output).doesNotContain("file1.txt", "file2.txt");
    }

    @Test
    void whenDirectoryDoesNotExistLsShowsError() {
        List<String> directories = List.of(workingDirectory.resolve("nonexistent").toString());

        Command command = new LsCommand(directories, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("nonexistent: No such directory");
    }
}
