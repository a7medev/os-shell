package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class MoveCommand implements Command {
    public static final String NAME = "mv";

    List<String> filePaths;
    String workingDirectory;
    PrintWriter outputWriter;
    PrintWriter errorWriter;
    Scanner inputScanner;
    boolean force;

    public MoveCommand(List<String> filePaths, boolean force, String workingDirectory, PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        this.filePaths = filePaths;
        this.force = force;
        this.workingDirectory = workingDirectory;
        this.outputWriter = outputWriter;
        this.errorWriter = errorWriter;
        this.inputScanner = inputScanner;
    }

    @Override
    public void execute() {
        if (filePaths.size() < 2) {
            errorWriter.println("usage: mv [-f] source target");
            errorWriter.println("       mv [-f] source ... directory");
            return;
        }

        List<File> files = filePaths.stream()
                .map(path -> FileUtils.fileInWorkingDirectory(path, workingDirectory))
                .toList();

        File source = files.get(0);
        File target = files.get(1);
        String sourcePath = filePaths.get(0);
        String targetPath = filePaths.get(1);

        if (files.size() == 2 && source.isFile() && (target.isFile() || !target.exists())) {
            if (target.exists() && !confirmOverwrite(targetPath)) {
                return;
            }

            boolean renameSucceeded = source.renameTo(target);

            if (!renameSucceeded) {
                errorWriter.println(NAME + ": failed to move " + sourcePath + " to " + targetPath);
            }
        } else if (files.size() == 2 && source.isDirectory() && !target.exists()) {
            boolean renameSucceeded = source.renameTo(target);

            if (!renameSucceeded) {
                errorWriter.println(NAME + ": failed to move " + sourcePath + " to " + targetPath);
            }
        } else {
            File directory = files.get(files.size() - 1);
            String directoryPath = filePaths.get(files.size() - 1);
            if (!directory.isDirectory() || !directory.exists()) {
                errorWriter.println(NAME + ": " + directoryPath + " is not a directory");
                return;
            }

            for (int i = 0; i < files.size() - 1; i++) {
                File file = files.get(i);
                String filePath = filePaths.get(i);

                moveToDirectory(file, directory, filePath, directoryPath);
            }
        }
    }

    private void moveToDirectory(File file, File directory, String filePath, String directoryPath) {
        File destination = new File(directory, file.getName());
        String relativeFilePath = new File(directoryPath, filePath).toString();

        if (destination.exists() && !confirmOverwrite(relativeFilePath)) {
            return;
        }

        boolean renameSucceeded = file.renameTo(destination);

        if (!renameSucceeded) {
            errorWriter.println(NAME + ": failed to move " + filePath + " into " + directoryPath);
        }
    }

    private boolean confirmOverwrite(String fileName) {
        if (force) {
            return true;
        }

        outputWriter.print(fileName + " already exists, do you want to overwrite it? (y/N) ");
        outputWriter.flush();
        String result = inputScanner.nextLine();

        return result.toLowerCase().startsWith("y");
    }
}
