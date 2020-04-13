package daytrader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Parser {
  private Map<String, Object> symbolTable;
  
  public Parser(Map<String, Object> symbolTable) {
    this.symbolTable = symbolTable;
  }
  
  public void parse(Iterator<Token> itr) throws Exception {
    List<Token> statement = new ArrayList<Token>();
    while (true) {
      if (!itr.hasNext()) {
        throw new Exception("unexpected end of file");
      }
      Token tk = itr.next();
      if (tk.getType() == Token.SEMI) {
        break;
      }
      statement.add(tk);
    }
    Object val = expression(statement.iterator());
    System.out.println(val);
  }
  
  private Object expression(Iterator<Token> itr) throws Exception {
    return primary(itr);
  }
  
  private Object primary(Iterator<Token> itr) throws Exception {
    Token tk = itr.next();
    if (tk.getType() == Token.INTEGER) {
      return tk.getValue();
    }
    if (tk.getType() == Token.REAL) {
      return tk.getValue();
    }
    if (tk.getType() == Token.SYMBOL) {
      if (!itr.hasNext()) {
        if (symbolTable.get(tk.getValue()) == null) {
          throw new Exception("uninitialized symbol: " + tk.getValue());
        }
        return symbolTable.get(tk.getValue());
      }
      String name = (String) tk.getValue();
      tk = itr.next();
      if (tk.getType() == Token.ASSIGN) {
        Object val = expression(itr);
        symbolTable.put(name, val);
        return val;
      }
    }
    return null;
  }
}
