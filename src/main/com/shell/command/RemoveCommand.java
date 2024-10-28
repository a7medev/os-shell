package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import static com.shell.util.ConfirmationUtils.confirmOverwrite;

public class RemoveCommand implements Command {
    public static final String NAME = "rm";

    private final List<String> filePaths;
    private final String workingDirectory;
    private final boolean force;
    private final boolean allowDirectories;

    public RemoveCommand(List<String> filePaths, boolean force, boolean allowDirectories, String workingDirectory) {
        this.filePaths = filePaths;
        this.force = force;
        this.allowDirectories = allowDirectories;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        if (filePaths.isEmpty()) {
            errorWriter.println("usage: rm [-f] [-r] file ...");
            return;
        }

        List<File> files = FileUtils.filesInWorkingDirectory(filePaths, workingDirectory);

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            String filePath = filePaths.get(i);

            if (!file.exists()) {
                if (!force) {
                    errorWriter.println(NAME + ": " + filePath + ": No such file or directory");
                }
                continue;
            }

            if (file.isDirectory() && !allowDirectories) {
                errorWriter.println(NAME + ": " + filePath + ": Is a directory");
                continue;
            }

            if (!confirmOverwrite(filePath, force, outputWriter, inputScanner)) {
                continue;
            }

            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file.toPath());
                } catch (IOException e) {
                    errorWriter.println(NAME + ": " + filePath + ": failed to delete directory");
                }
            } else {
                boolean isRemoved = file.delete();

                if (!isRemoved) {
                    errorWriter.println(NAME + ": " + filePath + ": failed to delete file");
                }
            }
        }
    }
}
