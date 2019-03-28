import lexanalyzer.Tokenizer;
import lexanalyzer.enums.TokenType;
import lexanalyzer.models.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please ensure your file is in range. And enter it's path:");
        String filePath = scanner.nextLine();

        System.out.println("Reading input...");
        ArrayList<String> lines = readFile(filePath);
        System.out.println("Input scanned");

        if (lines == null) {
            System.out.println("Something went wrong. Exiting");
            return;
        }

        String concatenated = concatLines(lines);

        System.out.println("Writing output files to output/ directory...\n" +
                "In case of failure please make sure I have the permission to make a directory");
        analyze(concatenated);
        System.out.println("Done");

    }

    private static ArrayList<String> readFile(String path) {
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            ArrayList<String> result = new ArrayList<>();

            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                result.add(currentLine);
            }

            return result;
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String concatLines(ArrayList<String> lines) {
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append(line);
            result.append('\n');
        }
        return result.toString();
    }

    private static void analyze(String input) {
        Formatter tokenFormatter = null, errorFormatter = null;
        try {
            File outputDirectory = new File("output");
            if (!outputDirectory.exists())
                outputDirectory.mkdir();
            tokenFormatter = new Formatter(new FileOutputStream("output/scanner.txt"));
            errorFormatter = new Formatter(new FileOutputStream("output/error.txt"));

            Tokenizer tokenizer = new Tokenizer(input);
            ArrayList<Token> tokens = tokenizer.tokenizeLine();
            while (tokens.get(0).getType() != TokenType.EOF) {
                boolean hasTokens = false, hasErrors = false;
                for (Token token : tokens) {
                    if (token.isError()) {
                        if (!hasErrors) {
                            errorFormatter.format(tokenizer.getLineNumber() + ". ");
                            hasErrors = true;
                        }
                        errorFormatter.format(token + " ");
                    } else if (!token.isWhite()) {
                        if (!hasTokens) {
                            tokenFormatter.format(tokenizer.getLineNumber() + ". ");
                            hasTokens = true;
                        }
                        tokenFormatter.format(token + " ");
                    }
                }
                if (hasTokens)
                    tokenFormatter.format("\n");
                if (hasErrors)
                    errorFormatter.format("\n");
                tokens = tokenizer.tokenizeLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (tokenFormatter != null)
                tokenFormatter.close();
            if (errorFormatter != null)
                errorFormatter.close();
        }

    }
}
