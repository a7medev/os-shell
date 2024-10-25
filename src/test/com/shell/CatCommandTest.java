package com.shell;

import com.shell.command.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class CatCommandTest {
    static final String FILE_CONTENT = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Praesent eros mi, imperdiet et eros vitae, fringilla porttitor neque.
            
            Suspendisse quis dignissim odio, ac consequat ipsum.""";

    Path workingDirectory;
    Path filePath;

    StringWriter outputStringWriter;
    StringWriter errorStringWriter;
    PrintWriter outputWriter;
    PrintWriter errorWriter;

    @BeforeEach
    void setUp() throws IOException {
        workingDirectory = Files.createTempDirectory("working-directory");
        filePath = Files.createTempFile(workingDirectory, "file", ".txt");

        Files.writeString(filePath, FILE_CONTENT);

        outputStringWriter = new StringWriter();
        errorStringWriter = new StringWriter();
        outputWriter = new PrintWriter(outputStringWriter);
        errorWriter = new PrintWriter(errorStringWriter);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(filePath);
        Files.delete(workingDirectory);
        outputWriter.close();
        errorWriter.close();
    }

    @Test
    void whenFileExistsGivenFilePathCatOutputsFileContents() {
        Command command = new CatCommand(
                filePath.toString(),
                workingDirectory.toString(),
                outputWriter,
                errorWriter);

        command.execute();

        assertThat(outputStringWriter.toString()).isEqualTo(FILE_CONTENT);
        assertThat(errorStringWriter.toString()).isEmpty();
    }

    @Test
    void whenFileDoesNotExistGivenFilePathCatOutputsAnError() {
        Command command = new CatCommand(
                "non-existent.txt",
                workingDirectory.toString(),
                outputWriter,
                errorWriter);

        command.execute();

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("No such file or directory");
    }

    @Test
    void whenFilePathIsADirectoryGivenFilePathCatOutputsAnError() {
        Command command = new CatCommand(
                "./",
                workingDirectory.toString(),
                outputWriter,
                errorWriter);

        command.execute();

        assertThat(outputStringWriter.toString()).isEmpty();
        assertThat(errorStringWriter.toString()).contains("Is a directory");
    }
}
