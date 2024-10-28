package com.shell.command;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
public class ListCommand implements Command {
    public static final String NAME = "ls";

    private final List<String> arguments;
    private final String workingDirectory;

    public ListCommand(List<String> arguments, String workingDirectory) {
        this.arguments = arguments;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        // Determine if "-a" or "-r" flags are present
        boolean showAll = arguments.contains("-a");
        boolean reverse = arguments.contains("-r");

        // Filter out flags to get directory paths
        List<String> directories = new ArrayList<>(arguments);
        directories.removeIf(arg -> arg.equals("-a") || arg.equals("-r"));

        // If no directories are specified, use the working directory
        if (directories.isEmpty()) {
            listDirectory(workingDirectory, showAll, reverse, outputWriter, errorWriter);
        } else {
            for (String dirPath : directories) {
                File dir = new File(workingDirectory, dirPath);
                listDirectory(dir.getPath(), showAll, reverse, outputWriter, errorWriter);
            }
        }
    }

    private void listDirectory(String dirPath, boolean showAll, boolean reverse, PrintWriter outputWriter, PrintWriter errorWriter) {
        File directory = new File(dirPath);

        if (directory.isDirectory()) {
            // Retrieve list of files with filtering and sorting applied
            String[] files = directory.list();
            if (files != null) {
                Stream<String> fileStream = Arrays.stream(files);

                // Apply `-a` option: filter hidden files if `showAll` is false
                if (!showAll) {
                    fileStream = fileStream.filter(file -> !file.startsWith("."));
                }

                // Convert stream back to list for sorting if `-r` is true
                List<String> fileList = new ArrayList<>(fileStream.toList());
                if (reverse) {
                    Collections.reverse(fileList);  // Reverse if `-r` option is set
                }

                // Output each file to outputWriter
                fileList.forEach(outputWriter::println);
            } else {
                errorWriter.println(NAME + ": " + dirPath + ": Could not retrieve directory contents");
            }
        } else {
            errorWriter.println(NAME + ": " + dirPath + ": No such directory");
        }
    }
}
