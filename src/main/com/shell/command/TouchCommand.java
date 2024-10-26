package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class TouchCommand implements Command {
    public static final String NAME = "touch";

    private final List<String> files;
    private final String workingDirectory;

    public TouchCommand(List<String> files, String workingDirectory) {
        this.files = files;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        for (String filePath : files) {
            File file = FileUtils.fileInWorkingDirectory(filePath, workingDirectory);

            try {
                file.createNewFile();
            } catch (IOException e) {
                errorWriter.println(NAME + ": " + filePath + ": Failed to create file");
            }
        }
    }
}
