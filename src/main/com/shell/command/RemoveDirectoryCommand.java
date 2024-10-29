package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class RemoveDirectoryCommand implements Command {
    public static final String NAME = "rmdir";

    private final List<String> directories;
    private final String workingDirectory;

    public RemoveDirectoryCommand(List<String> directories, String workingDirectory) {
        this.directories = directories;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        for (String dir : directories) {
//            Path directoryPath;
//
//            if (Paths.get(dir).isAbsolute()) {
//                directoryPath = Paths.get(dir);
//            } else {
//                directoryPath = Paths.get(workingDirectory).resolve(dir);
//            }
            File directoryFile = FileUtils.fileInWorkingDirectory(dir, workingDirectory);
            Path directoryPath = directoryFile.toPath();

            try {
                if (!Files.exists(directoryPath)) {
                    errorWriter.println(NAME + ": " + dir + " Directory does not exist");
                } else if (!Files.isDirectory(directoryPath)) {
                    errorWriter.println(NAME + ": " + dir + " is not a directory");
                } else if (Files.list(directoryPath).findAny().isPresent()) {
                    errorWriter.println(NAME + ": " + dir + " Directory is not empty");
                } else {
                    Files.delete(directoryPath);
//                    outputWriter.println("Directory removed: " + directoryPath);
                }
            } catch (IOException e) {
                errorWriter.println(NAME + ": " + dir + ": Failed to remove directory");
            }
        }
    }
}
