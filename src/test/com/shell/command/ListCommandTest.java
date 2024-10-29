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

class ListCommandTest {
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
        Files.createFile(workingDirectory.resolve(".hiddenfile"));
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
    void whenDirectoryExistsListCommandListsFiles() {
        List<String> arguments = List.of();

        Command command = new ListCommand(arguments, false, false, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("file1.txt", "file2.txt", "subdir");
        assertThat(output).doesNotContain(".hiddenfile");
    }

    @Test
    void whenDirectoryExistsWithAFlagListCommandListsAllFilesIncludingHidden() {
        List<String> arguments = List.of();
        Command command = new ListCommand(arguments, true, false, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("file1.txt", "file2.txt", "subdir", ".hiddenfile");
    }

    @Test
    void whenDirectoryExistsWithRFlagListCommandListsFilesInReverseOrder() {
        List<String> arguments = List.of();
        Command command = new ListCommand(arguments, false, true, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();

        assertThat(output).isEqualToIgnoringNewLines("""
                subdir
                file2.txt
                file1.txt
                """);
    }

    @Test
    void whenDirectoryExistsWithAAndRFlagsListCommandListsAllFilesInReverseOrderIncludingHidden() {
        List<String> arguments = List.of();
        Command command = new ListCommand(arguments, true, true, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString().trim();
        assertThat(errorStringWriter.toString()).isEmpty();

        // Check that files are in reverse order and include the hidden file
        assertThat(output).isEqualToIgnoringNewLines("""
                subdir
                file2.txt
                file1.txt
                .hiddenfile
                """);
    }

    @Test
    void whenSubdirectorySpecifiedListCommandListsFilesInSubdirectory() {
        List<String> directories = List.of(subdirectory.toString());
        Command command = new ListCommand(directories, false, false, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        String output = outputStringWriter.toString();
        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(output).contains("file3.txt");
        assertThat(output).doesNotContain("file1.txt", "file2.txt", ".hiddenfile");
    }

    @Test
    void whenDirectoryDoesNotExistListCommandShowsError() {
        List<String> directories = List.of(workingDirectory.resolve("nonexistent").toString());
        Command command = new ListCommand(directories, false, false, workingDirectory.toString());
        command.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("nonexistent: No such directory");
    }
}
