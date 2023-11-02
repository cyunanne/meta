package netty._test;

import java.io.Serializable;

public class Header implements Serializable {

    private static final long serialVersionUID = 123L;
    public static final int HEADER_SIZE = 58;
    private char type;
    private int size;

    public Header(char type, int size) {
        this.type = type;
        this.size = size;
    }

    public char getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}
