package parser.models;

import io.OutputHandler;
import lexanalyzer.Tokenizer;
import lexanalyzer.models.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Set<String> nonTerminals = new HashSet<>();
    private static Set<String> terminals = new HashSet<>();

    private static HashMap<String, Set<String>> firsts = new HashMap<>();
    private static HashMap<String, Set<String>> follows = new HashMap<>();

    private static HashMap<String, List<Rule>> rules = new HashMap<>();

    // All of this has been obtained from using the grammar on this website : https://mikedevice.github.io/first-follow/
    // Grammar used will be included

    // Initialize terminals and non-terminals
    private static void loadSymbols(String filePath, Set<String> symbolSet) {
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() > 0)
                    symbolSet.add(line);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // initializing first and follow
    private static void loadSetFromFile(String filePath, HashMap<String, Set<String>> sets) {
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() > 0) {
                    String key = null;
                    HashSet<String> values = new HashSet<>();
                    Matcher matcher = Pattern.compile("(\\S+):| (\\S+)").matcher(line);
                    while (matcher.find()) {
                        if (key == null)
                            key = matcher.group(1);
                        else
                            values.add(matcher.group(2));
                    }
                    sets.put(key, values);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // initializing rules
    private static void loadRules() {
        try {
            FileInputStream inputStream = new FileInputStream("resources/rules.txt");
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() > 0) {
                    String key = null;
                    List<String> rule = new ArrayList<>();
                    Matcher matcher = Pattern.compile("(\\S+) ->| (\\S+)").matcher(line);
                    while (matcher.find()) {
                        if (key == null)
                            key = matcher.group(1);
                        else
                            rule.add(matcher.group(2));
                    }
                    if (rules.containsKey(key))
                        rules.get(key).add(new Rule(rule));
                    else {
                        List<Rule> ruleList = new ArrayList<>(Collections.singletonList(new Rule(rule)));
                        rules.put(key, ruleList);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Initializing firsts
    private static void loadFirsts() {
        loadSetFromFile("resources/firsts.txt", firsts);
        for (String terminal : terminals) {
            firsts.put(terminal, new HashSet<>(Collections.singletonList(terminal)));
        }
    }

    // Initializing follows
    private static void loadFollows() {
        loadSetFromFile("resources/follows.txt", follows);
    }


    static {
        loadSymbols("resources/non-terminals.txt", nonTerminals);
        loadSymbols("resources/terminals.txt", terminals);
        loadFirsts();
        loadFollows();
        loadRules();
    }


    private Tokenizer tokenizer;
    private Token token;
    private int depth = -1;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;

        // Initialize token
        this.token = tokenizer.getNextToken();
    }


    public void startParse() {
        parse("Program");
    }

    private void parse(String state) {
        depth++;
        OutputHandler.getInstance().printParser(state, depth);

        if (state.equals("eps")) {
            depth--;
            return;
        }


        if (!terminals.contains(state) && !nonTerminals.contains(state)) {
            System.err.println("WHAT THE FUCK???");
            depth--;
            return;
        }

        // Parse one terminal
        if (terminals.contains(state)) {
            if (isTokenMatch(state)) {

                // This token has been parsed
                if (!token.isEOF())
                    token = tokenizer.getNextToken();
            } else {
                System.err.println("Terminal state isn't the token");
            }
        }

        // Parse a non terminal
        if (nonTerminals.contains(state)) {
            Rule goalRule = null;
            for (Rule rule : rules.get(state)) {
                if (isRuleValid(state, rule)) {
                    goalRule = rule;
                    break;
                }
            }

            // TODO: 5/17/19 this if is redundant
            if (goalRule != null) {
                for (String component : goalRule.getComponents()) {

                    if (terminals.contains(component)) {
                        if (isTokenMatch(component) || component.equals("eps")) {
                            parse(component);
                        } else {
                            if (component.equals("eof"))
                                OutputHandler.getInstance().printError(token.getLineNumber() +
                                        ": Syntax Error! Malformed Inout");
                            else
                                OutputHandler.getInstance().printError(token.getLineNumber() +
                                        ": Syntax Error! Missing" + component);
                        }
                    }


                    if (nonTerminals.contains(component)) {
                        Set<String> first = firsts.get(component), follow = follows.get(component);
                        while (!first.contains(getTokenString()) && !follow.contains(getTokenString())) {
                            OutputHandler.getInstance().printError(token.getLineNumber() +
                                    ": Syntax Error! Unexpected " + token.getType());
                            if (!token.isEOF())
                                token = tokenizer.getNextToken();
                            else {
                                return;
                            }
                        }
                        if (!first.contains(getTokenString()) && !first.contains("eps")) {
                            OutputHandler.getInstance().printError(token.getLineNumber() +
                                    ": Syntax Error! Missing " + component);
                        } else {
                            parse(component);
                        }
                    }
                }
            } else {
                // None of the rules match
                System.err.println("Didn't expect this!");
            }
        }

        depth--;
    }

    private boolean isRuleValid(String state, Rule rule) {
        String tokenString = getTokenString();

        Set<String> first = rule.getFirst();

        boolean isInFirst = first.contains(tokenString);
        boolean isInFollow = first.contains("eps") && follows.get(state).contains(tokenString);

        return isInFirst || isInFollow;
    }

    private String getTokenString() {
        switch (token.getType()) {
            case SYMBOL:
            case EOF:
            case KEYWORD:
                return token.getText().trim().toLowerCase();

            case NUM:
                return "num";
            case ID:
                return "id";
            default:
                return "";
        }
    }

    private boolean isTokenMatch(String state) {
        return state.equals(getTokenString());
    }


    static class Rule {
        private List<String> comps;

        Rule(List<String> comps) {
            this.comps = comps;
        }

        List<String> getComponents() {
            return this.comps;
        }

        Set<String> getFirst() {
            Set<String> first = new HashSet<>();
            boolean isNullable = true;
            for (String compoennt : comps) {
                first.addAll(firsts.get(compoennt));
                if (first.contains("eps"))
                    first.remove("eps");
                else {
                    isNullable = false;
                    break;
                }
            }
            if (isNullable)
                first.add("eps");
            return first;
        }
    }
}