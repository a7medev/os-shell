package com.shell.command;


import java.util.List;
import java.io.*;
import java.util.Scanner;
public class LsCommand implements Command {
    public static final String NAME = "ls";

    private final List<String> directories;  // List of directories to list
    private final String workingDirectory;   // Default directory if none is specified

    public LsCommand(List<String> directories, String workingDirectory) {
        this.directories = directories;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        // If no directories specified, default to the working directory
        if (directories.isEmpty()) {
            listDirectory(workingDirectory, outputWriter, errorWriter);
        } else {
            for (String dirPath : directories) {
                File dir = new File(workingDirectory, dirPath);
                listDirectory(dir.getPath(), outputWriter, errorWriter);
            }
        }
    }

    // Method to list files in a given directory
    private void listDirectory(String dirPath, PrintWriter outputWriter, PrintWriter errorWriter) {
        File directory = new File(dirPath);

        if (directory.isDirectory()) {
            // List the contents of the directory
            String[] files = directory.list();
            if (files != null) {
                for (String file : files) {
                    outputWriter.println(file);
                }
            } else {
                errorWriter.println(NAME + ": " + dirPath + ": Could not retrieve directory contents");
            }
        } else {
            errorWriter.println(NAME + ": " + dirPath + ": No such directory");
        }
    }
}
