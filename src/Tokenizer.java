import Enums.TokenType;
import Models.Token;

import java.util.ArrayList;
import java.util.Hashtable;

public class Tokenizer {

    private static int counter;
    private static Hashtable<String, Integer> states = new Hashtable<>();
    private static Hashtable<Integer, TokenType> acceptStates = new Hashtable<>();
    private static int[][] nextStates;

    static {
        init();
    }

    private static void init() {
        int counter = 0;
        String[] keywords = {"if", "else", "void", "int", "while", "break", "continue", "switch", "default", "case", "return"};

        counter = initHashtables(keywords);
        initNextStates(counter, keywords);
    }

    private static int initHashtables(String[] keywords) {
        int counter = 0;

        states.put("START", counter);
        counter++;


        for (String keyword : keywords) {
            for (int i = 0; i < keyword.length(); i++) {
                if (!states.containsKey(keyword.substring(0, i + 1))) {
                    states.put(keyword.substring(0, i + 1), counter);
                    if (i + 1 == keyword.length()) {
                        acceptStates.put(counter, TokenType.KEYWORD);
                    } else {
                        acceptStates.put(counter, TokenType.ID);
                    }
                    counter++;
                }
            }
        }

        // ID
        states.put("ID", counter);
        acceptStates.put(counter, TokenType.ID);
        counter++;

        // NUM
        states.put("NUM", counter);
        acceptStates.put(counter, TokenType.NUM);
        counter++;

        // Symbols (1 letter) and ==
        states.put("SYMBOL", counter);
        acceptStates.put(counter, TokenType.SYMBOL);
        counter++;

        states.put("EQUALS", counter);
        acceptStates.put(counter, TokenType.SYMBOL);
        counter++;


        // All 1 letter whitespaces
        states.put("WHITESPACE", counter);
        acceptStates.put(counter, TokenType.WHITESPACE);
        counter++;

        // All Kinds of comments
        states.put("CommentStart", counter);
        counter++;

        states.put("InlineCommentStart", counter);
        counter++;

        states.put("InlineCommentAccept", counter);
        acceptStates.put(counter, TokenType.COMMENT);
        counter++;

        states.put("MultilineCommentStart", counter);
        counter++;

        states.put("MultilineCommentMid", counter);
        counter++;

        states.put("MultilineCommentAccept", counter);
        acceptStates.put(counter, TokenType.COMMENT);
        counter++;

        return counter;
    }

    private static void initNextStates(int counter, String[] keywords) {
        // 128 is the length of the ascii table. Anything other than that is definitely an invalid char.
        // This will be our table for finding the next state.
        nextStates = new int[counter][128];

        // -2 Means automatic invalid character
        for (int i = 0; i < counter; i++) {
            for (int j = 0; j < 128; j++) {
                nextStates[i][j] = -2;
            }
        }

        // -1 means not defined in table
        int[] validSymbols = {';', ':', ',', '[', ']', '(', ')', '{', '}', '+', '-', '*', '=', '<', '/', 32, 10, 13, 9, 11, 12};
        int[] validChars = {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        };
        int[] validNums = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        for (int i = 0; i < counter; i++) {
            for (int valid : validSymbols) {
                nextStates[i][valid] = -1;
            }
            for (int valid : validChars) {
                nextStates[i][valid] = -1;
            }
            for (int valid : validNums) {
                nextStates[i][valid] = -1;
            }
        }

        // Fill in table

        // NUM
        for (int i = 0; i <= 9; i++) {
            nextStates[states.get("START")]['0' + i] = states.get("NUM");
            nextStates[states.get("NUM")]['0' + i] = states.get("NUM");
        }

        // Symbol
        int[] symbols = {';', ':', ',', '[', ']', '(', ')', '{', '}', '+', '-', '*', '=', '<'};
        for (int symbol : symbols) {
            nextStates[states.get("START")][symbol] = states.get("SYMBOL");
        }
        nextStates[states.get("SYMBOL")]['='] = states.get("EQUALS");


        // IDs and Keywords

        for (int validChar : validChars) {
            nextStates[states.get("START")][validChar] = states.get("ID");
        }
        for (String keyword : keywords) {
            for (int i = 0; i < keyword.length(); i++) {
                String key = keyword.substring(0, i + 1);
                for (int validChar : validChars) {
                    nextStates[states.get(key)][validChar] = states.get("ID");
                }
                for (int validNum : validNums) {
                    nextStates[states.get(key)][validNum] = states.get("ID");
                }
            }
        }

        for (String keyword : keywords) {
            nextStates[states.get("START")][keyword.charAt(0)] = states.get(keyword.substring(0, 1));
            for (int i = 0; i < keyword.length(); i++) {
                String key = keyword.substring(0, i + 1);
                if (i + 1 < keyword.length()) {
                    nextStates[states.get(key)][keyword.charAt(i + 1)] = states.get(keyword.substring(0, i + 2));
                }
            }
        }

        for (int validChar : validChars) {
            nextStates[states.get("ID")][validChar] = states.get("ID");
        }
        for (int validNum : validNums) {
            nextStates[states.get("ID")][validNum] = states.get("ID");
        }


        // Whitespaces
        int[] whitespaces = {32, 10, 13, 9, 11, 12};
        for (int whitespace : whitespaces) {
            nextStates[states.get("START")][whitespace] = states.get("WHITESPACE");
        }


        // Comments
        nextStates[states.get("START")]['/'] = states.get("CommentStart");

        nextStates[states.get("CommentStart")]['/'] = states.get("InlineCommentStart");
        for (int i = 0; i < 128; i++) {
            nextStates[states.get("InlineCommentStart")][i] = states.get("InlineCommentStart");
        }
        nextStates[states.get("InlineCommentStart")]['\n'] = states.get("InlineCommentAccept");


        nextStates[states.get("CommentStart")]['*'] = states.get("MultilineCommentStart");
        for (int i = 0; i < 128; i++) {
            nextStates[states.get("MultilineCommentStart")][i] = states.get("MultilineCommentStart");
        }
        nextStates[states.get("MultilineCommentStart")]['*'] = states.get("MultilineCommentMid");

        for (int i = 0; i < 128; i++) {
            nextStates[states.get("MultilineCommentMid")][i] = states.get("MultilineCommentStart");
        }
        nextStates[states.get("MultilineCommentMid")]['*'] = states.get("MultilineCommentMid");
        nextStates[states.get("MultilineCommentMid")]['/'] = states.get("MultilineCommentAccept");
    }

    private static Token getNextToken(String file) {
        int bestSoFar = -1;
        TokenType bestToken = null;

        // Start in start state
        int state = states.get("START");

        int innerCounter = counter - 1;

        while (true) {
            innerCounter++;
            state = nextStates[state][file.charAt(innerCounter)];

            if (acceptStates.containsKey(state)) {
                bestSoFar = innerCounter + 1;
                bestToken = acceptStates.get(state);
            }

            // Cannot proceed in dfs
            if (state < 0) {
                break;
            }

            if (innerCounter + 1 == file.length()) {
                break;
            }
        }

        // Couldn't parse
        if (bestSoFar == -1) {
            String text;

            // Invalid input encountered
            if (state == -2) {
                text = file.substring(counter, innerCounter + 1);
                counter = innerCounter + 1;
                return new Token(text, TokenType.InvalidInput);
            }

            // Stuck in DFS
            if (state == -1) {
                text = file.substring(counter, innerCounter + 1);
                counter = innerCounter + 1;
                return new Token(text, TokenType.StuckWhileParsing);
            }

            // This means we are in the middle of the parsing and haven't accepted yet but have reached end of the file
            if (state > 0) {
                text = file.substring(counter);
                counter = file.length();
                return new Token(text, TokenType.MidParseEOF);
            }
            throw new RuntimeException("What the hell is going on?");
        }

        String text = file.substring(counter, bestSoFar);
        counter = bestSoFar;
        return new Token(text, bestToken);
    }

    public static ArrayList<Token> tokenize(String file) {
        counter = 0;
        ArrayList<Token> tokens = new ArrayList<>();
        while (counter < file.length()) {
            tokens.add(getNextToken(file));
        }
        return tokens;
    }
}
