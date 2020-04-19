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
  
  public void parse(Token tk, TokenIterator itr) throws Exception {
    while (true) {
      if (tk.getType() == Token.CHART) {
        throw new Exception("unsupported");
      } else {
        List<Token> list = new ArrayList<Token>();
        list.add(tk);
        while (itr.hasNext() && itr.peek().getType() != Token.SEMI) {
          list.add(itr.next());
        }
        if (!itr.hasNext()) {
          log.error("missing semi colon");
          throw new Exception("syntax error");
        }
        itr.next();
        TokenIterator itr2 = new TokenIterator(list);
        Token tk2 = itr2.next();
        if (tk.getType() == Token.CONST) {
          parseDeclaration(tk2, itr2);
        } else {
          expression(tk2, itr2);
        }
      }
      if (!itr.hasNext()) {
        break;
      }
      tk = itr.next();
    }
  }
  
  private void parseDeclaration(Token tk, TokenIterator itr) throws Exception {
    // skip const
    tk = itr.next();
    if (!itr.hasNext()) {
      log.error("invalid const declaration");
      throw new Exception("syntax error");
    }
    tk = itr.next();
    if (tk.getType() != Token.SYMBOL) {
      log.error("invalid const declaration");
      throw new Exception("syntax error");
    }
    String symbolName = (String) tk.getValue();
    if (!itr.hasNext()) {
      log.error("invalid const declaration");
      throw new Exception("syntax error");
    }
    tk = itr.next();
    if (tk.getType() != Token.ASSIGN) {
      log.error("invalid const declaration");
      throw new Exception("syntax error");
    }
    if (!itr.hasNext()) {
      log.error("invalid const declaration");
      throw new Exception("syntax error");
    }
    tk = itr.next();
    Object val = expression(tk, itr);
    symbolTable.put((String) symbolName, val);
  }
  
  private Object expression(Token tk, TokenIterator itr) throws Exception {
    Object val1 = term(tk, itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      if (itr.peek().getType() == Token.PLUS) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on PLUS");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = term(tk, itr);
        if (val1 instanceof String) {
          val1 = val1 + val2.toString();
        } else if (val1 instanceof Long && val2 instanceof Long) {
          val1 = new Long((Long) val1 + (Long) val2);
        } else if (val1 instanceof Long && val2 instanceof Double) {
          val1 = new Double((Long) val1 + (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Long) {
          val1 = new Double((Double) val1 + (Long) val2);
        } else if (val1 instanceof Double && val2 instanceof Double) {
          val1 = new Double((Double) val1 + (Double) val2);
        } else {
          log.error("invalid PLUS operation");
          throw new Exception("syntax error");
        }
      } else if (itr.peek().getType() == Token.MINUS) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on MINUS");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = term(tk, itr);
        if (val1 instanceof String) {
          throw new Exception("unsupported string operation: " + val1);
        } else if (val1 instanceof Long && val2 instanceof Long) {
          val1 = new Long((Long) val1 - (Long) val2);
        } else if (val1 instanceof Long && val2 instanceof Double) {
          val1 = new Double((Long) val1 - (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Long) {
          val1 = new Double((Double) val1 - (Long) val2);
        } else if (val1 instanceof Double && val2 instanceof Double) {
          val1 = new Double((Double) val1 - (Double) val2);
        } else {
          log.error("invalid MINUS operation");
          throw new Exception("syntax error");
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object term(Token tk, TokenIterator itr) throws Exception {
    Object val1 = primary(tk, itr);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      if (itr.peek().getType() == Token.MULT) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on MULT");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = primary(tk, itr);
        if (val1 instanceof Long && val2 instanceof Long) {
          val1 = new Long((Long) val1 * (Long) val2);
        } else if (val1 instanceof Long && val2 instanceof Double) {
          val1 = new Double((Long) val1 * (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Long) {
          val1 = new Double((Double) val1 * (Long) val2);
        } else if (val1 instanceof Double && val2 instanceof Double) {
          val1 = new Double((Double) val1 * (Double) val2);
        } else {
          log.error("invalid MULT operation");
          throw new Exception("syntax error");
        }
      } else if (itr.peek().getType() == Token.EXP) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on EXP");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = primary(tk, itr);
        if (val1 instanceof Long) {
          val1 = ((Long) val1).doubleValue();
        }
        if (val2 instanceof Long) {
          val2 = ((Long) val2).doubleValue();
        }
        if (!(val1 instanceof Double) || !(val2 instanceof Double)) {
          log.error("exponentials must be double");
          throw new Exception("syntax error");
        }
        val1 = Math.pow((Double) val1, (Double) val2);
      } else if (itr.peek().getType() == Token.DIV) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on DIV");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = primary(tk, itr);
        if (val1 instanceof Long && val2 instanceof Long) {
          if ((Long) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          if ((Long) val1 % (Long) val2 == 0) {
            val1 = new Long((Long) val1 / (Long) val2);
          } else {
            val1 = new Double(((Long) val1).doubleValue() / ((Long) val2).doubleValue());
          }
        } else if (val1 instanceof Long && val2 instanceof Double) {
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Long) val1 / (Double) val2);
        } else if (val1 instanceof Double && val2 instanceof Long) {
          if ((Long) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Double((Double) val1 / (Long) val2);
        } else if (val1 instanceof Double && val2 instanceof Double) {
          val1 = new Double((Double) val1 / (Double) val2);
          if ((Double) val2 == 0d) {
            throw new Exception("divide by 0 error");
          }
        } else {
          log.error("invalid DIV operation");
          throw new Exception("syntax error");
        }
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object primary(Token tk, TokenIterator itr) throws Exception {
    if (tk.getType() == Token.INTEGER || tk.getType() == Token.REAL || tk.getType() == Token.STRING) {
      return tk.getValue();
    }
    if (tk.getType() == Token.PLUS) {
      tk = itr.next();
      Object val = primary(tk, itr);
      if (!(val instanceof Long) && !(val instanceof Double)) {
        log.error("invalid unary plus");
        throw new Exception("syntax error");
      }
      return val;
    }
    if (tk.getType() == Token.MINUS) {
      tk = itr.next();
      Object val = primary(tk, itr);
      if (val instanceof Long) {
        return new Long(-(Long) val);
      } else if (val instanceof Double) {
        return new Double(-(Double) val);
      } else {
        log.error("invalid unary minus");
        throw new Exception("syntax error");
      }
    }
    if (tk.getType() == Token.SYMBOL) {
      String symbolName = (String) tk.getValue();
      if (itr.hasNext() && itr.peek().getType() == Token.ASSIGN) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on ASSIGN");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val = expression(tk, itr);
        symbolTable.put((String) symbolName, val);
      }
      Object val = symbolTable.get(symbolName);
      if (val == null) {
        throw new Exception("uninitialized symbol: " + tk.getValue());
      }
      return val;
    }
    if (tk.getType() == Token.LPAREN) {
      tk = itr.next();
      Object val = expression(tk, itr);
      if (!itr.hasNext()) {
        log.error("missing RPAREN");
        throw new Exception("syntax error: unmatched lparen");
      }
      itr.next();
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
      tk = itr.next();
      List<Object> params = new ArrayList<Object>();
      while (tk.getType() != Token.RPAREN) {
        Object val = expression(tk, itr);
        params.add(val);
        if (!itr.hasNext()) {
          log.error("unexpected end of input: " + funcName);
          throw new Exception("syntax error: " + funcName);
        }
        tk = itr.next();
        if (tk.getType() == Token.COMMA) {
          if (!itr.hasNext()) {
            log.error("unexpected end of input: " + funcName);
            throw new Exception("syntax error: " + funcName);
          }
          tk = itr.next();
          if (tk.getType() == Token.RPAREN) {
            log.error("unexpected comma: " + funcName);
            throw new Exception("syntax error: " + funcName);
          }
        }
      }
      return funcCaller.invokeFunction(funcName, params);
    }
    throw new Exception("unsupported primary expression: " + tk);
  }
}
