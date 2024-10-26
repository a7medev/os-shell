package com.shell.command;

import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateCommand implements Command {
    public static String NAME = "date";

    @Override
    public void execute (PrintWriter outputWriter, PrintWriter errorWriter, Scanner inputScanner) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd-MM-yyyy HH:mm:ss");
        String date = now.format(formatter);

        outputWriter.println(date);
    }
}
