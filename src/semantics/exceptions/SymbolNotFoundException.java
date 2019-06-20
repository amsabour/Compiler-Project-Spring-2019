package semantics.exceptions;

public class SymbolNotFoundException extends Exception {
    private String name;

    public SymbolNotFoundException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
