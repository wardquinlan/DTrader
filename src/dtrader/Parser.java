package dtrader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Parser {
  private static Log log = LogFactory.getFactory().getInstance(Parser.class);
  private static FunctionCaller funcCaller = new FunctionCaller();
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
        log.error("unexpected end of file");
        throw new Exception("unexpected end of file");
      }
      Token tk = itr.next();
      if (tk.getType() == Token.SEMI) {
        break;
      }
      statement.add(tk);
    }
    Object val = expression(new TokenIterator(statement));
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
        if (val1 instanceof String) {
          val1 = val1 + val2.toString();
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
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
        if (val1 instanceof String) {
          throw new Exception("unsupported string operation: " + val1);
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
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
            throw new Exception("divide by 0 error");
          }
          if ((Integer) val1 % (Integer) val2 == 0) {
            val1 = new Integer((Integer) val1 / (Integer) val2);
          } else {
            val1 = new Double(((Integer) val1).doubleValue() / ((Integer) val2).doubleValue());
          }
        } else if (val1 instanceof Integer && val2 instanceof Double) {
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Integer) val1 / (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Double) val1 / (Integer) val2);
        } else {
          val1 = new Double((Double) val1 / (Double) val2);
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
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
    if (tk.getType() == Token.INTEGER || tk.getType() == Token.REAL || tk.getType() == Token.STRING) {
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
    if (tk.getType() == Token.FUNC) {
      String funcName = (String) tk.getValue();
      if (!itr.hasNext()) {
        log.error("unexpected end of input: " + funcName);
        throw new Exception("syntax error: " + funcName);
      }
      tk = itr.next();
      if (tk.getType() != Token.LPAREN) {
        log.error("expecting left parenthesis: " + funcName);
        throw new Exception("syntax error: " + funcName);
      }
      if (!itr.hasNext() || itr.peek().getType() == Token.COMMA) {
        log.error("unexpected comma or end of input: " + funcName);
        throw new Exception("syntax error: " + funcName);
      }
      List<Object> params = new ArrayList<Object>();
      while (itr.hasNext() && itr.peek().getType() != Token.RPAREN) {
        Object val = expression(itr);
        params.add(val);
        if (!itr.hasNext()) {
          log.error("unexpected end of input: " + funcName);
          throw new Exception("syntax error: " + funcName);
        }
        tk = itr.next();
        if (tk.getType() != Token.COMMA && tk.getType() != Token.RPAREN) {
          log.error("expecting comma or right parenthesis: " + funcName);
          throw new Exception("syntax error: " + funcName);
        }
        if (tk.getType() == Token.COMMA && (!itr.hasNext() || itr.peek().getType() == Token.RPAREN)) {
          log.error("unexpected comma: " + funcName);
          throw new Exception("syntax error: " + funcName);
        }
      }
      funcCaller.invokeFunction(funcName, params);
      return new Integer(0);
    }
    throw new Exception("unsupported primary: " + tk);
  }
}
