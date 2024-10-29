package com.shell.command;

import com.shell.CommandLineInterpreter;
import com.shell.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeDirectoryCommandTest {
    Path rootDirectory;
    Path subDirectory;

    StringWriter outputStringWriter;
    PrintWriter outputWriter;
    StringWriter errorStringWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;
    String currentDirectory;
    CommandLineInterpreter interpreter;

    @BeforeEach
    void setUp() throws IOException {
        rootDirectory = Files.createTempDirectory("root-directory");
        subDirectory = Files.createDirectory(rootDirectory.resolve("subdir"));

        currentDirectory = rootDirectory.toString();

        outputStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorStringWriter = new StringWriter();
        errorWriter = new PrintWriter(errorStringWriter);
        inputScanner = new Scanner("");

        interpreter = new CommandLineInterpreter(currentDirectory,
                "user",
                "/Users/user",
                "Darwin",
                outputWriter,
                errorWriter,
                inputScanner);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(rootDirectory);
        outputWriter.close();
        errorWriter.close();
        inputScanner.close();
    }

    @Test
    void whenValidDirectoryChangeDirectoryUpdatesCurrentDirectory() {
        ChangeDirectoryCommand changeDirectoryCommand = new ChangeDirectoryCommand(subDirectory.toString(), currentDirectory, interpreter);

        changeDirectoryCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(interpreter.getWorkingDirectory()).isEqualTo(subDirectory.toString());
    }

    @Test
    void whenInvalidDirectoryChangeDirectoryShowsError() {
        String invalidPath = rootDirectory.resolve("nonexistent").toString();
        ChangeDirectoryCommand changeDirectoryCommand = new ChangeDirectoryCommand(invalidPath, currentDirectory, interpreter);

        changeDirectoryCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains(invalidPath + " No such directory");
        assertThat(interpreter.getWorkingDirectory()).isEqualTo(currentDirectory);
    }

    @Test
    void whenParentDirectorySymbolUsedChangeDirectoryGoesToParent() {
        ChangeDirectoryCommand changeDirectoryCommand = new ChangeDirectoryCommand("..", subDirectory.toString(), interpreter);

        changeDirectoryCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(errorStringWriter.toString()).isEmpty();
        assertThat(interpreter.getWorkingDirectory()).isEqualTo(rootDirectory.toString());
    }

    @Test
    void whenUserDoesNotSpecifyDirectoryChangeDirectoryShowsError(){
        ChangeDirectoryCommand changeDirectoryCommand = new ChangeDirectoryCommand(" ", currentDirectory, interpreter);

        changeDirectoryCommand.execute(outputWriter, errorWriter, inputScanner);

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("cd: Please specify a directory");
        assertThat(interpreter.getWorkingDirectory()).isEqualTo(currentDirectory);
    }
}
