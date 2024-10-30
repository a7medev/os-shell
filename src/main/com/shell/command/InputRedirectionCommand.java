package com.shell.command;

import com.shell.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class InputRedirectionCommand implements Command {
    final private String filePath;
    final private String workingDirectory;
    final private Command cmd;

    public InputRedirectionCommand(String filePath, String workingDirectory, Command cmd) {
        this.filePath = filePath;
        this.workingDirectory = workingDirectory;
        this.cmd = cmd;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        File file = FileUtils.fileInWorkingDirectory(filePath, workingDirectory);

        try (Scanner fileScanner = new Scanner(file)) {
            cmd.execute(outputWriter, errorWriter, fileScanner);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
