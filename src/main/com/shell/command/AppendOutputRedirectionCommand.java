package com.shell.command;

import com.shell.util.FileUtils;

import java.io.*;
import java.util.Scanner;

public class AppendOutputRedirectionCommand implements Command {
    final private String filePath;
    final private String workingDirectory;
    final private Command cmd;

    public AppendOutputRedirectionCommand(String filePath, String workingDirectory, Command cmd) {
        this.filePath = filePath;
        this.workingDirectory = workingDirectory;
        this.cmd = cmd;
    }

    @Override
    public void execute(PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        File file = FileUtils.fileInWorkingDirectory(filePath, workingDirectory);

        try (PrintWriter fileWriter = new PrintWriter(new FileWriter(file, true))) {
            cmd.execute(fileWriter, errorWriter, inputScanner);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
