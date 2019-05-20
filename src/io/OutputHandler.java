package io;

import java.io.*;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputHandler implements Closeable {
    private Formatter parserFormatter;

    private Formatter errorFormatter;

    private HashMap<String, String> nonTerminalDescriptions = new HashMap<>();

    private static OutputHandler instance;


    private OutputHandler() {
        try {
            Scanner scanner = new Scanner(new FileInputStream("resources/nonterminal_descriptions.txt"));
            while (scanner.hasNextLine()) {
                Matcher matcher = Pattern.compile("(\\S+): (.+)").matcher(scanner.nextLine());
                if (matcher.find())
                    nonTerminalDescriptions.put(matcher.group(1), matcher.group(2));
                else
                    System.err.println("Error in reading nonterminals_descriptions");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    public void printLexicalError(String errorType, int lineNumber) {
        errorFormatter.format(lineNumber + ": Lexical Error! " + errorType + "\n");
    }

    public void printNonTerminalError(String nonTerminal, int lineNumber) {
        errorFormatter.format(lineNumber + ": Syntax Error! Missing " +
                nonTerminalDescriptions.get(nonTerminal) + "\n");
    }

    public void printTerminalError(String terminal, String errorType, int lineNumber) {
        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + " " + terminal + "\n");
    }

    public void printEOFError(String errorType, int lineNumber) {
        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + "\n");
    }

    @Override
    public void close() {
        parserFormatter.close();
        errorFormatter.close();
    }
}
