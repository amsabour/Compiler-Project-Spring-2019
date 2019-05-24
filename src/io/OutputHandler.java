package io;

import lexanalyzer.models.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.Error.ErrorType;
import static io.Error.ErrorType.LexicalError;
import static io.Error.ErrorType.SyntaxError;

public class OutputHandler implements Closeable {
    private Formatter parserFormatter;
    private Formatter lexerFormatter;
    private Formatter errorFormatter;

    private HashMap<String, String> nonTerminalDescriptions = new HashMap<>();

    private ArrayList<Error> errorBuffer;
    private ArrayList<Token> tokenBuffer;
    private int errorLineNumber;
    private int lexerLineNumber;

    private static OutputHandler instance;

    private OutputHandler() {
        errorBuffer = new ArrayList<>();
        tokenBuffer = new ArrayList<>();
        errorLineNumber = 1;
        lexerLineNumber = 1;
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
            lexerFormatter = new Formatter(new FileOutputStream("output/scanner.txt"));
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

    public void printLexer(Token token) {
        flushIfNewLexerLine(token.getLineNumber());
        tokenBuffer.add(token);
    }

    public void printLexicalError(String errorType, int lineNumber) {
        flushIfNewErrorLine(lineNumber);
        addToErrorBuffer(errorType, LexicalError);
//        errorFormatter.format(lineNumber + ": Lexical Error! " + errorType + "\n");
    }

    public void printNonTerminalError(String nonTerminal, int lineNumber) {
        flushIfNewErrorLine(lineNumber);
        addToErrorBuffer("Missing " + nonTerminalDescriptions.get(nonTerminal), SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! Missing " +
//                nonTerminalDescriptions.get(nonTerminal) + "\n");
    }

    public void printTerminalError(String terminal, String errorType, int lineNumber) {
        flushIfNewErrorLine(lineNumber);
        addToErrorBuffer(errorType + " " + terminal, SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + " " + terminal + "\n");
    }

    public void printEOFError(String errorType, int lineNumber) {
        flushIfNewErrorLine(lineNumber);
        addToErrorBuffer(errorType, SyntaxError);
//        errorFormatter.format(lineNumber + ": Syntax Error! " + errorType + "\n");
    }

    private void addToErrorBuffer(String error, ErrorType type) {
        errorBuffer.add(new Error(error, type));
    }

    private void flushIfNewErrorLine(int lineNumber) {
        if (lineNumber > errorLineNumber) {
            flushErrorBuffer();
            errorLineNumber = lineNumber;
        }
    }

    private void flushIfNewLexerLine(int lineNumber) {
        if (lineNumber > lexerLineNumber) {
            flushLexerBuffer();
            lexerLineNumber = lineNumber;
        }
    }

    public void flushErrorBuffer() {
        ArrayList<String> lexErrors = new ArrayList<>();
        ArrayList<String> syntaxErrors = new ArrayList<>();

        for (Error error : errorBuffer) {
            switch (error.getType()) {
                case LexicalError:
                    lexErrors.add(error.getMessage());
                    break;
                case SyntaxError:
                    syntaxErrors.add(error.getMessage());
                    break;
            }
        }

        if (!lexErrors.isEmpty()) {
            errorFormatter.format("Line " + errorLineNumber + " lexical errors: ");
            prettyPrintErrors(lexErrors);
            errorFormatter.format("\n");
        }

        if (!syntaxErrors.isEmpty()) {
            errorFormatter.format("Line " + errorLineNumber + " syntax errors: ");
            prettyPrintErrors(syntaxErrors);
            errorFormatter.format("\n");
        }

        errorBuffer.clear();
    }

    private void flushLexerBuffer() {
        lexerFormatter.format(lexerLineNumber + ". ");
        for (Token token : tokenBuffer) {
            lexerFormatter.format("(" + token.getType() + ", " + token.getText() + ") ");
        }
        lexerFormatter.format("\n");
        tokenBuffer.clear();
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
        lexerFormatter.close();
        errorFormatter.close();
    }

}

