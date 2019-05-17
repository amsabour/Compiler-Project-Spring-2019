package io;

import java.io.*;
import java.util.Formatter;

public class OutputHandler implements Closeable {
    private Formatter parserFormatter;

    private Formatter errorFormatter;

    private static OutputHandler instance;

    private OutputHandler() {
        File outputDirectory = new File("output");
        if (!outputDirectory.exists())
            outputDirectory.mkdir();
        try {
            parserFormatter = new Formatter(new FileOutputStream("output/parser.txt"));
            errorFormatter = new Formatter(new FileOutputStream("output/error.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static OutputHandler getInstance() {
        if (instance == null) {
            instance = new OutputHandler();
        }
        return instance;
    }

    public void printParser(String state, int depth) {
        for (int i = 0; i < depth; i++) {
            parserFormatter.format("|\t");
        }
        parserFormatter.format(state + "\n");
    }

    public void printError(String message) {
        errorFormatter.format(message + "\n");
    }

    @Override
    public void close(){
        parserFormatter.close();
        errorFormatter.close();
    }
}
