package com.tomkp.moxy.readers;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RelativeFileReader {

    public InputStream readRelativeFile(String resourcePath, String filename) throws IOException {
        File file = new File(resourcePath, filename);
        InputSupplier<FileInputStream> inputSupplier = Files.newInputStreamSupplier(file);
        return inputSupplier.getInput();
    }
}