package daytrader;

import java.util.HashMap;
import java.util.Map;

public class Token {
  public static final int SYMBOL   = 0;  // some_symbol
  public static final int DOUBLE   = 1;  // 3.14159
  public static final int WHOLE    = 2;  // 14
  public static final int STRING   = 3;  // "hello"
  public static final int LPAREN   = 4;  // (
  public static final int RPAREN   = 5;  // )
  public static final int LBRACE   = 6;  // {
  public static final int RBRACE   = 7;  // }
  public static final int ASSIGN   = 8;  // =
  public static final int EQ       = 9;  // ==
  public static final int NE       = 10; // !=
  public static final int LT       = 11; // <
  public static final int LTE      = 12; // <=
  public static final int GT       = 13; // >
  public static final int GTE      = 14; // >=
  public static final int SEMI     = 16; // ;
  public static final int COMMA    = 17; // ,
  public static final int COMMENT  = 18; // --
  public static final int PLUS     = 19; // +
  public static final int MINUS    = 20; // - 
  public static final int MULT     = 21; // *
  public static final int DIVIDE   = 22; // /
  public static final int EXP      = 23; // **
  
  public static Map<Integer, String> map = new HashMap<Integer, String>();
  static {
    map.put(SYMBOL, "SYMBOL");
    map.put(STRING, "STRING");
  }
  
  private int type;
  private Object value;
	
  public Token(int type) {
    this.type = type;
  }
	
  public int getType() {
    return type;
  }
  
  public Object getValue() {
    return value;
  }
  
  public void setValue(Object object) {
    value = object;
  }
  
  @Override
  public String toString() {
    return "Token[" + map.get(type) + "]=" + value;
  }
}
