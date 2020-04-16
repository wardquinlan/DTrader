package dtrader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Parser {
  private static Log log = LogFactory.getFactory().getInstance(Parser.class);
  
  private Map<String, Object> symbolTable;
  
  public Parser(Map<String, Object> symbolTable) {
    this.symbolTable = symbolTable;
  }
  
  public void parse(Iterator<Token> itr) throws Exception {
    while (itr.hasNext()) {
      parseStatement(itr);
    }
  }
  
  public void parseStatement(Iterator<Token> itr) throws Exception {
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
    Object val = expression(new TokenIterator(statement));
    System.out.println(val);
  }
  
  private Object expression(TokenIterator itr) throws Exception {
    Object val1 = term(itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      Token tk = itr.peek();
      if (tk.getType() == Token.PLUS) {
        itr.next();
        Object val2 = term(itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 + (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 + (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 + (Integer) val2);
        } else {
          val1 = new Double((Double) val1 + (Double) val2);
        }
      } else if (tk.getType() == Token.MINUS) {
        itr.next();
        Object val2 = term(itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 - (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 - (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 - (Integer) val2);
        } else {
          val1 = new Double((Double) val1 - (Double) val2);
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object term(TokenIterator itr) throws Exception {
    Object val1 = primary(itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      Token tk = itr.peek();
      if (tk.getType() == Token.MULT) {
        itr.next();
        Object val2 = primary(itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 * (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          val1 = new Double((Integer) val1 * (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          val1 = new Double((Double) val1 * (Integer) val2);
        } else {
          val1 = new Double((Double) val1 * (Double) val2);
        }
      }
      else if (tk.getType() == Token.DIV) {
        itr.next();
        Object val2 = primary(itr);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0");
          }
          val1 = new Integer((Integer) val1 / (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0");
          }
          val1 = new Double((Integer) val1 / (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0");
          }
          val1 = new Double((Double) val1 / (Integer) val2);
        } else {
          val1 = new Double((Double) val1 / (Double) val2);
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0");
          }
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object primary(TokenIterator itr) throws Exception {
    if (!itr.hasNext()) {
      throw new Exception("unexpected end of statement");
    }
    Token tk = itr.next();
    if (tk.getType() == Token.INTEGER) {
      return tk.getValue();
    }
    if (tk.getType() == Token.REAL) {
      return tk.getValue();
    }
    if (tk.getType() == Token.SYMBOL) {
      if (itr.hasNext() && itr.peek().getType() == Token.ASSIGN) {
        itr.next();
        Object val = expression(itr);
        symbolTable.put((String) tk.getValue(), val);
        return val;
      }
      Object val = symbolTable.get(tk.getValue());
      if (val == null) {
        throw new Exception("uninitialized symbol: " + tk.getValue());
      }
      return val;
    }
    throw new Exception("unsupported primary: " + tk);
  }
}
