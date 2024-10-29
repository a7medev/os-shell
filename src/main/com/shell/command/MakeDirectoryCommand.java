package com.shell.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class MakeDirectoryCommand implements Command {
    public static final String NAME = "mkdir";

    private final List<String> directories;
    private final String workingDirectory;

    public MakeDirectoryCommand(List<String> directories, String workingDirectory) {
        this.directories = directories;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        for (String dir : directories) {
            Path directoryPath;

            if (Paths.get(dir).isAbsolute()) {
                directoryPath = Paths.get(dir);
            } else {
                directoryPath = Paths.get(workingDirectory).resolve(dir);
            }

            try {
                if (Files.exists(directoryPath)) {
                    errorWriter.println(NAME + ": " + dir + " Directory already exists");
                } else {
                    Files.createDirectories(directoryPath);
                    outputWriter.println("Directory created: " + directoryPath);
                }
            } catch (IOException e) {
                errorWriter.println(NAME + ": " + dir + ": Failed to create directory");
            }
        }
    }
}
