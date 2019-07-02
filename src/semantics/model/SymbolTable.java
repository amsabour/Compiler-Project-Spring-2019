package semantics.model;

import semantics.exceptions.SymbolNameTakenException;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> table = new HashMap<>();
    private String functionName = null;

    private boolean breakable = false;
    private int breakAddress = -1;

    public SymbolTable() {

    }

    public SymbolTable(int breakAddress) {
        this.breakable = true;
        this.breakAddress = breakAddress;
    }


    public void addSymbol(String name, SymbolType type, int address) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(type, address));
    }

    public void addVoidFunction(String name, int startAddress, int returnAddress) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(startAddress, returnAddress));

        if (functionName == null)
            functionName = name;
        else {
            System.err.println("This is wrong. 2 original functions in 1 symbolTable????????");
        }
    }

    public void addIntFunction(String name, int startAddress, int returnAddress, int returnValueAddress) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(startAddress, returnAddress, returnValueAddress));

        if (functionName == null)
            functionName = name;
        else {
            System.err.println("This is wrong. 2 original functions in 1 symbolTable????????");
        }
    }

    public void putFunction(String name, Symbol symbol) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);

        table.put(name, symbol);
    }

    public SymbolType getType(String name) {
        return table.get(name).getType();
    }

    public Integer getAddress(String name) {
        return table.get(name) != null ? table.get(name).getAddress() : null;
    }

    public Symbol getSymbol(String name) {
        return table.get(name);
    }

    public String getFunction() {
        return this.functionName;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public int getBreakAddress() {
        return breakAddress;
    }

}

