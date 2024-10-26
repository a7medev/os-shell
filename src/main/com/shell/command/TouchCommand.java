package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TouchCommand implements Command {
    public static final String NAME = "touch";

    List<String> files;
    String workingDirectory;
    PrintWriter errorWriter;

    TouchCommand(List<String> files, String workingDirectory, PrintWriter errorWriter) {
        this.files = files;
        this.workingDirectory = workingDirectory;
        this.errorWriter = errorWriter;
    }

    @Override
    public void execute() {
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
