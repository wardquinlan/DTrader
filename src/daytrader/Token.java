package daytrader;

public class Token {
  public static final int STRING = 0;
  public static final int LITERAL = 1;
  public static final int SYMBOL = 2;
	
  private int type;
	
  public Token(int type) {
    this.type = type;
  }
	
  public int getType() {
    return type;
  }
}
