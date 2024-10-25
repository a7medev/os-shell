package com.shell.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileUtils {

    /**
     * Resolves a file path to a <code>File</code> instance given a working directory
     * taking into account whether the file path is an absolute or relative path.
     *
     * @param path Relative or absolute file path.
     * @param workingDirectory Working directory to find relative file paths in.
     * @return The correct file path based on whether it's absolute or relative.
     */
    public static File fileInWorkingDirectory(String path, String workingDirectory) {
        File filePath = new File(path);

        if (filePath.isAbsolute()) {
            return filePath;
        }

        return new File(workingDirectory, path);
    }

    public static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
                stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
