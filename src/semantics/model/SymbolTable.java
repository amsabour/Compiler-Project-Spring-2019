package semantics.model;

import semantics.exceptions.SymbolNameTakenException;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> table = new HashMap<>();

    public void addSymbol(String name, SymbolType type, int address) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(type, address));
    }

    public void addVoidFunction(String name, int startAddress, int returnAddress) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(startAddress, returnAddress));
    }

    public void addIntFunction(String name, int startAddress, int returnAddress, int returnValueAddress) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(startAddress, returnAddress, returnValueAddress));
    }

    public void putFunction(String name, Symbol symbol) throws SymbolNameTakenException {
        if(table.containsKey(name))
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


}

