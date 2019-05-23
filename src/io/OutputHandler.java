package io;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputHandler implements Closeable {
    private Formatter parserFormatter;

    private Formatter errorFormatter;

    private HashMap<String, String> nonTerminalDescriptions = new HashMap<>();

    private ArrayList<Pair<String, ErrorType>> errorBuffer;
    private int bufferLineNumber;

    private static OutputHandler instance;

    private OutputHandler() {
        errorBuffer = new ArrayList<>();
        bufferLineNumber = 1;
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
        flushIfNewline(lineNumber);
        addToBuffer(errorType, ErrorType.LexicalError);
//        errorFormatter.format(lineNumber + ": Lexical Error! " + errorType + "\n");
    }

    public void printNonTerminalError(String nonTerminal, int lineNumber) {
        flushIfNewline(lineNumber);
        addToBuffer(nonTerminalDescriptions.get(nonTerminal), ErrorType.SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! Missing " +
//                nonTerminalDescriptions.get(nonTerminal) + "\n");
    }

    public void printTerminalError(String terminal, String errorType, int lineNumber) {
        flushIfNewline(lineNumber);
        addToBuffer(errorType + " " + terminal, ErrorType.SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + " " + terminal + "\n");
    }

    public void printEOFError(String errorType, int lineNumber) {
        flushIfNewline(lineNumber);
        addToBuffer(errorType, ErrorType.SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + "\n");
    }

    private void addToBuffer(String error, ErrorType type) {
        errorBuffer.add(new Pair<>(error, type));
    }

    private void flushIfNewline(int lineNumber) {
        if (lineNumber > bufferLineNumber) {
            flushErrorBuffer();
            bufferLineNumber = lineNumber;
        }
    }

    public void flushErrorBuffer() {
        ArrayList<String> lexErrors = new ArrayList<>();
        ArrayList<String> syntaxErrors = new ArrayList<>();

        for (Pair<String, ErrorType> error : errorBuffer) {
            switch (error.getValue()) {
                case LexicalError:
                    lexErrors.add(error.getKey());
                    break;
                case SyntaxError:
                    syntaxErrors.add(error.getKey());
                    break;
            }
        }

        if (!lexErrors.isEmpty()) {
            errorFormatter.format("Line " + bufferLineNumber + " lexical errors: ");
            prettyPrintErrors(lexErrors);
            errorFormatter.format("\n");
        }

        if (!syntaxErrors.isEmpty()) {
            errorFormatter.format("Line " + bufferLineNumber + " syntax errors: ");
            prettyPrintErrors(syntaxErrors);
            errorFormatter.format("\n");
        }

        errorBuffer.clear();
    }

    private void prettyPrintErrors(ArrayList<String> errors) {
        for (int i = 0; i < errors.size(); i++) {
            String error = errors.get(i);
            errorFormatter.format(error);
            if (i + 1 < errors.size())
                errorFormatter.format(" , ");
        }
    }

    @Override
    public void close() {
        parserFormatter.close();
        errorFormatter.close();
    }

    static enum ErrorType {
        LexicalError,
        SyntaxError
    }
}

