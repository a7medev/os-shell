package com.shell.util;

import java.io.PrintWriter;
import java.util.Scanner;

public class ConfirmationUtils {
    public static boolean confirmOverwrite(String fileName, boolean force, PrintWriter outputWriter, Scanner inputScanner) {
        if (force) {
            return true;
        }

        outputWriter.print(fileName + " already exists, do you want to overwrite it? (y/N) ");
        outputWriter.flush();
        String result = inputScanner.nextLine();

        return result.toLowerCase().startsWith("y");
    }
}
