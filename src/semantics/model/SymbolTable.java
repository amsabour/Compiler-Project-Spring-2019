package semantics.model;

import semantics.exceptions.SymbolNameTakenException;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> table;

    public void addSymbol(String name, SymbolType type, int address) throws SymbolNameTakenException {
        if (table.containsKey(name))
            throw new SymbolNameTakenException(name);
        table.put(name, new Symbol(type, address));
    }

    public SymbolType getType(String name) {
        return table.get(name).getType();
    }

    public Integer getAddress(String name) {
        return table.get(name).getAddress();
    }


    private class Symbol {
        private SymbolType type;
        private int address;
        // TODO Add something here

        Symbol(SymbolType type, int address) {
            this.type = type;
            this.address = address;
        }

        SymbolType getType() {
            return type;
        }

        int getAddress() {
            return address;
        }
    }
}
