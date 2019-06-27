package semantics;

import semantics.exceptions.SymbolNameTakenException;
import semantics.exceptions.SymbolNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;

public class SemanticAnalyzer {
    private MemoryHandler memoryHandler = new MemoryHandler();
    private Stack<String> semanticStack = new Stack<>();
    private ArrayList<String> programBlock = new ArrayList<>();

    // Program counter
    private int i = 0;

    public void callRoutine(String name, String input) {
        try {
            if (name.equals("push") || name.equals("pid"))
                getClass().getDeclaredMethod(name, String.class).invoke(this, input);
            else
                getClass().getDeclaredMethod(name).invoke(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String ss(int fromTop) {
        if (fromTop == 0)
            return semanticStack.peek();
        else
            return semanticStack.get(semanticStack.size() - fromTop - 1);
    }

    private void pop(int n) {
        for (int j = 0; j < n; j++) {
            semanticStack.pop();
        }
    }

    // TODO Handle this somehow....
    // the following methods are semantic routines

    void has_main() {
        System.out.println("Semantic routine called: has_dec");
    }

    void push(String input) {
        System.out.println("Semantic routine called: push");
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
        System.out.println("Semantic routine called: var_dec");
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
        System.out.println("Semantic routine called: arr_dec");
    }

    void func_dec_begin() {
        System.out.println("Semantic routine called: func_dec_begin");
    }

    void func_dec_end() {
        System.out.println("Semantic routine called: func_dec_end");
    }

    void add_func_to_symbol_table() {
        System.out.println("Semantic routine called: add_func_to_symbol_table");
    }

    void arr_param() {
        System.out.println("Semantic routine called: arr_param");
    }

    void var_param() {
        System.out.println("Semantic routine called: var_param");
    }

    void begin() {
        System.out.println("Semantic routine called: begin");
    }

    void end() {
        System.out.println("Semantic routine called: end");
    }

    void continuez() {
        System.out.println("Semantic routine called: continue");
    }

    void breakz() {
        System.out.println("Semantic routine called: break");
    }

    void save() {
        System.out.println("Semantic routine called: save");
    }

    void jpf_save() {
        System.out.println("Semantic routine called: jpf_save");
    }

    void jp() {
        System.out.println("Semantic routine called: jp");
    }

    void label() {
        System.out.println("Semantic routine called: label");
    }

    void whilez() {
        System.out.println("Semantic routine called: while");
    }

    void return_void() {
        System.out.println("Semantic routine called: return_void");
    }

    void return_expr() {
        System.out.println("Semantic routine called: return_expr");
    }

    void switchz() {
        System.out.println("Semantic routine called: switch");
    }

    void start_scope_breakable() {
        System.out.println("Semantic routine called: start_scope_breakable");
    }

    void end_scope() {
        System.out.println("Semantic routine called: end_scope");
    }

    void pop() {
        System.out.println("Semantic routine called: pop");
    }

    void cmp() {
        System.out.println("Semantic routine called: cmp");
    }

    void jpf() {
        System.out.println("Semantic routine called: jpf");
    }

    void update_addr() {
        System.out.println("Semantic routine called: update_addr]");
    }

    void calc() {
        System.out.println("Semantic routine called: calc");
    }

    void push_minusone_mult() {
        System.out.println("Semantic routine called: push_minusone_mult");
    }

    void pid(String input) {
        System.out.println("Semantic routine called: pid");
        try {
            semanticStack.push("" + memoryHandler.findAddress(input));
        } catch (SymbolNotFoundException e) {
            e.printStackTrace();
        }
    }

    void get_arr_element() {
        System.out.println("Semantic routine called: get_arr_element");
        int t = memoryHandler.getTemp();
        programBlock.add(i, "(ADD," + ss(1) + "," + ss(0) + "," + t + ")");
        i++;
        pop(2);
        semanticStack.push("" + t);
    }

    void start_set_param() {
        System.out.println("Semantic routine called: start_set_param");
    }

    void end_set_param() {
        System.out.println("Semantic routine called: end_set_param");
    }

    void call() {
        System.out.println("Semantic routine called: call");
    }

    void set_param() {
        System.out.println("Semantic routine called: set_param");
    }

    void assign() {
        System.out.println("Semantic routine called: assign");
        programBlock.add(i, "(ASSIGN," + ss(0) + "," + ss(1) + ",)");
        i++;
    }

}
