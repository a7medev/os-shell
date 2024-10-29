package com.shell.command;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ChangeDirectoryCommand implements Command {
    public static final String NAME = "cd";

    private final String targetDirectory;
    private String currentDirectory;

    public ChangeDirectoryCommand(String targetDirectory, String currentDirectory) {
        this.targetDirectory = targetDirectory;
        this.currentDirectory = currentDirectory;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        Path newDirectoryPath;

        if (targetDirectory.equals("..")) {
            newDirectoryPath = Paths.get(currentDirectory).getParent();
        }

        else if (Paths.get(targetDirectory).isAbsolute()) {
            newDirectoryPath = Paths.get(targetDirectory);
        }
        else {
            newDirectoryPath = Paths.get(currentDirectory).resolve(targetDirectory).normalize();
        }

        if (newDirectoryPath != null && newDirectoryPath.toFile().exists() && newDirectoryPath.toFile().isDirectory()) {
            currentDirectory = newDirectoryPath.toString();
        } else {
            errorWriter.println("No such directory: " + targetDirectory);
        }
    }
}
