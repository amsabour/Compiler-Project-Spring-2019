package semantics.exceptions;

public class FunctionNotFoundException extends Exception {
    private int address;

    public FunctionNotFoundException(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
