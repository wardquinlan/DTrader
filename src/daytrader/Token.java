package daytrader;

import java.util.HashMap;
import java.util.Map;

public class Token {
  public static final int SYMBOL   = 0;  // some_symbol
  public static final int NUMBER   = 1;  // 3.14159
  public static final int STRING   = 2;  // "hello"
  public static final int LPAREN   = 3;  // (
  public static final int RPAREN   = 4;  // )
  public static final int LBRACE   = 5;  // {
  public static final int RBRACE   = 6;  // }
  public static final int ASSIGN   = 7;  // =
  public static final int EQ       = 8;  // ==
  public static final int NE       = 9;  // !=
  public static final int LT       = 10; // <
  public static final int LTE      = 11; // <=
  public static final int GT       = 12; // >
  public static final int GTE      = 13; // >=
  public static final int SEMI     = 14; // ;
  public static final int COMMA    = 15; // ,
  public static final int COMMENT  = 16; // --
  public static final int PLUS     = 17; // +
  public static final int MINUS    = 18; // - 
  public static final int MULT     = 19; // *
  public static final int DIVIDE   = 20; // /
  public static final int EXP      = 21; // **
  
  public static Map<Integer, String> map = new HashMap<Integer, String>();
  static {
    map.put(SYMBOL,  "SYMBOL");
    map.put(NUMBER,  "NUMBER");
    map.put(STRING,  "STRING");
    map.put(LPAREN,  "LPAREN");
    map.put(RPAREN,  "RPAREN");
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
