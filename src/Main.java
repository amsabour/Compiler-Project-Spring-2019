import lexanalyzer.Tokenizer;
import lexanalyzer.models.Token;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please ensure your file is in range. And enter it's path:");
        String filePath = scanner.nextLine();

        ArrayList<String> lines = readFile(filePath);

        if (lines == null) {
            System.out.println("Something went wrong. Exiting");
            return;
        }

        String concatenated = concatLines(lines);

        ArrayList<Token> tokens = Tokenizer.tokenize(concatenated);
        for (Token token : tokens) {
            String tokenText = token.toString();
            if (tokenText.length() > 0)
                System.out.println(token);
        }
//        System.out.println("Reading File complete");
//        System.out.println("Tokenizing...");
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
        StringBuilder result = new StringBuilder("");

        for (String line : lines) {
            result.append(line);
            result.append('\n');
        }
        return result.toString();
    }

}
