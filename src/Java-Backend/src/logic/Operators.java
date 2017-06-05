package logic;


public enum  Operators {
    STAR(0),
    PLUS(1),
    QUESTION_MARK(2);

    private final int id;
    Operators(int id) { this.id = id;}
    public int getValue() { return id;}
}
