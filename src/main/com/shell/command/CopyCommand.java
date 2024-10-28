package com.shell.command;

import com.shell.util.FileUtils;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static com.shell.util.ConfirmationUtils.confirmOverwrite;

public class CopyCommand implements Command {
    public static final String NAME = "cp";

    private final List<String> filePaths;
    private final String workingDirectory;
    private final boolean force;

    public CopyCommand(List<String> filePaths, boolean force, String workingDirectory) {
        this.filePaths = filePaths;
        this.force = force;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        if (filePaths.size() < 2) {
            errorWriter.println("usage: cp [-f] source target");
            errorWriter.println("       cp [-f] source ... directory");
            return;
        }

        List<File> files = FileUtils.filesInWorkingDirectory(filePaths, workingDirectory);

        File source = files.get(0);
        File target = files.get(1);
        String sourcePath = filePaths.get(0);
        String targetPath = filePaths.get(1);

        if (files.size() == 2 && source.isFile() && (target.isFile() || !target.exists())) {
            if (target.exists() && !confirmOverwrite(targetPath, force, outputWriter, inputScanner)) {
                return;
            }

            try {
                FileUtils.copy(source, target);
            } catch (FileNotFoundException e) {
                errorWriter.println(NAME + ": " + sourcePath + ": no such file");
            } catch (IOException e) {
                errorWriter.println(NAME + ": failed to move " + sourcePath + " to " + targetPath);
            }
        } else {
            File directory = files.get(files.size() - 1);
            String directoryPath = filePaths.get(files.size() - 1);
            if (!directory.exists()) {
                errorWriter.println(NAME + ": " + directoryPath + ": no such file or directory");
                return;
            }
            if (!directory.isDirectory()) {
                errorWriter.println(NAME + ": " + directoryPath + ": is not a directory");
                return;
            }

            for (int i = 0; i < files.size() - 1; i++) {
                File file = files.get(i);
                String filePath = filePaths.get(i);

                copyToDirectory(file, directory, filePath, directoryPath, outputWriter, errorWriter, inputScanner);
            }
        }
    }

    private void copyToDirectory(File file, File directory, String filePath, String directoryPath, PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        File destination = new File(directory, file.getName());
        String relativeFilePath = new File(directoryPath, filePath).toString();

        if (destination.exists() && !confirmOverwrite(relativeFilePath, force, outputWriter, inputScanner)) {
            return;
        }

        try {
            FileUtils.copy(file, destination);
        } catch (FileNotFoundException e) {
            errorWriter.println(NAME + ": " + filePath + ": no such file");
        } catch (IOException e) {
            errorWriter.println(NAME + ": failed to copy " + filePath + " to " + directoryPath);
        }
    }
}
