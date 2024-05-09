package org.example.util;

import java.io.File;

public class Util {

    public static boolean isPDFFile(File file) {
        return file.getName().endsWith(".pdf");
    }
}
