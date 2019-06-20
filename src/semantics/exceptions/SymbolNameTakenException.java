package semantics.exceptions;

public class SymbolNameTakenException extends Exception {
    private String name;

    public SymbolNameTakenException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
