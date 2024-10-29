package com.shell.command;

import com.shell.util.FileUtils;

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

    private final List<String> directories;
    private final String workingDirectory;

    private final boolean showAll;
    private final boolean reverse;

    public ListCommand(List<String> directories,boolean showAll, boolean reverse, String workingDirectory) {
        this.directories = directories;
        this.workingDirectory = workingDirectory;
        this.showAll = showAll;
        this.reverse = reverse;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {

        if (directories.isEmpty()) {
            listDirectory(workingDirectory, outputWriter, errorWriter);
        } else {
            for (String dirPath : directories) {
                File dir = FileUtils.fileInWorkingDirectory(dirPath, workingDirectory);
                listDirectory(dir.getPath(), outputWriter, errorWriter);
            }
        }
    }

    private void listDirectory(String dirPath, PrintWriter outputWriter, PrintWriter errorWriter) {
        File directory = new File(dirPath);

        if (directory.isDirectory()) {
            String[] files = directory.list();
            if (files != null) {
                Stream<String> fileStream = Arrays.stream(files);

                if (!showAll) {
                    fileStream = fileStream.filter(file -> !file.startsWith("."));
                }

                List<String> fileList = new ArrayList<>(fileStream.sorted().toList());
                if (reverse) {
                    Collections.reverse(fileList);
                }

                fileList.forEach(outputWriter::println);
            } else {
                errorWriter.println(NAME + ": " + dirPath + ": Could not retrieve directory contents");
            }
        } else {
            errorWriter.println(NAME + ": " + dirPath + ": No such directory");
        }
    }
}
