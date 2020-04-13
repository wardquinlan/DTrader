package daytrader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {
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
    expression(statement.iterator());
  }
  
  private void expression(Iterator<Token> itr) throws Exception {
    primary(itr);
  }
  
  private void primary(Iterator<Token> itr) throws Exception {
    Token tk = itr.next();
    if (tk.getType() == Token.INTEGER) {
      System.out.println(tk);
      return;
    }
    if (tk.getType() == Token.REAL) {
      System.out.println(tk);
      return;
    }
    if (tk.getType() == Token.SYMBOL) {
      if (!itr.hasNext()) {
        System.out.println(tk);
        return;
      }
      tk = itr.next();
      if (tk.getType() == Token.ASSIGN) {
        expression(itr);
      }
    }
  }
}
