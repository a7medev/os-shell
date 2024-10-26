package com.shell.util;

import java.io.File;

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
}
