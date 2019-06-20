package semantics;

import semantics.exceptions.SymbolNameTakenException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;

public class SemanticAnalyzer {
    private MemoryHandler memoryHandler = new MemoryHandler();
    private Stack<String> semanticStack = new Stack<>();
    private ArrayList<String> programBlock = new ArrayList<>();

    void callRoutine(String name) {
        try {
            getClass().getDeclaredMethod(name).invoke(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // the following methods are semantic routines

    void push(String input) {
        semanticStack.push(input);
    }

    void var_dec() {
        String name = semanticStack.pop();
        String type = semanticStack.pop();
        if (type.equals("void")) {
            // TODO: 6/21/19 Error
        }
        try {
            memoryHandler.allocateVar(name);
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
        }
    }

    void arr_dec() {
        int size = Integer.parseInt(semanticStack.pop());
        String name = semanticStack.pop();
        String type = semanticStack.pop();
        if (type.equals("void")) {
            // TODO: 6/21/19 Error
        }
        try {
            memoryHandler.allocateArray(name, size);
        } catch (SymbolNameTakenException e) {
            // TODO: 6/21/19 Error
        }
    }
}
