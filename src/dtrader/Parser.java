package dtrader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Parser {
  private static Log log = LogFactory.getFactory().getInstance(Parser.class);
  private static FunctionCaller funcCaller = new FunctionCaller();

  public Scope parse(Token tk, TokenIterator itr) throws Exception {
    Scope root = new Scope();
    parseScope(tk, itr, root);
    return root;
    
    /*
    while (true) {
      if (tk.getType() == Token.CHART) {
        Chart chart = parseChart(tk, itr, root);
        root.getCharts().add(chart);
      } else {
        Statement statement = parseStatement(tk, itr, root);
        root.getStatements().add(statement);
      }
      if (!itr.hasNext()) {
        break;
      }
      tk = itr.next();
    }
    return root;
    */
  }
  
  private void parseScope(Token tk, TokenIterator itr, Scope scope) throws Exception {
    if (tk.getType() != Token.LBRACE) {
      log.error("missing left brace");
      throw new Exception("syntax error");
    }
    if (!itr.hasNext()) {
      log.error("missing left brace");
      throw new Exception("syntax error");
    }
    tk = itr.next();
    while (true) {
      if (tk.getType() == Token.RBRACE) {
        break;
      }
      if (tk.getType() == Token.CHART) {
        Chart chart = parseChart(tk, itr, scope);
        scope.getCharts().add(chart);
      } else {
        Statement statement = parseStatement(tk, itr, scope);
        scope.getStatements().add(statement);
      }
      if (!itr.hasNext()) {
        log.error("unexpected end of scope");
        throw new Exception("syntax error");
      }
      tk = itr.next();
    }
  }
  
  private Chart parseChart(Token tk, TokenIterator itr, Scope scope) throws Exception {
    if (!itr.hasNext()) {
      log.error("missing chart name");
      throw new Exception("syntax error: chart name");
    }
    tk = itr.next();
    if (tk.getType() != Token.SYMBOL) {
      log.error("invalid chart name");
      throw new Exception("syntax error: chart name");
    }
    String name = (String) tk.getValue();
    if (!itr.hasNext()) {
      log.error("missing left brace");
      throw new Exception("syntax error: chart");
    }
    tk = itr.next();
    if (tk.getType() != Token.LBRACE) {
      log.error("missing left brace");
      throw new Exception("syntax error: chart");
    }
    scope = new Scope(scope);
    parseScope(tk, itr, scope);
    return new Chart(name, scope);
  }
  
  private Statement parseStatement(Token tk, TokenIterator itr, Scope scope) throws Exception {
    Statement statement = new Statement();
    statement.getTokens().add(tk);
    while (itr.hasNext() && itr.peek().getType() != Token.SEMI) {
      statement.getTokens().add(itr.next());
    }
    if (!itr.hasNext()) {
      log.error("missing semi colon");
      throw new Exception("syntax error");
    }
    itr.next();
    TokenIterator itr2 = new TokenIterator(statement.getTokens());
    Token tk2 = itr2.next();
    if (tk.getType() == Token.CONST) {
      parseDeclaration(tk2, itr2, scope);
    } else {
      expression(tk2, itr2, scope);
    }
    if (itr2.hasNext()) {
      log.error("unexpected symbol at end of line");
      throw new Exception("syntax error");
    }
    return statement;
  }
  
  private void parseDeclaration(Token tk, TokenIterator itr, Scope scope) throws Exception {
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
    Object val = expression(tk, itr, scope);
    if (scope.getSymbol(symbolName) != null) {
      throw new Exception("symbol already defined: " + symbolName);
    }
    scope.setSymbol(symbolName, new Symbol(val, true));
  }
  
  private Object expression(Token tk, TokenIterator itr, Scope scope) throws Exception {
    Object val1 = term(tk, itr, scope);
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
        Object val2 = term(tk, itr, scope);
        if (val1 instanceof String) {
          val1 = val1 + val2.toString();
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 + (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Float) {
          val1 = new Float((Integer) val1 + (Float) val2);
        } else if (val1 instanceof Float && val2 instanceof Integer) {
          val1 = new Float((Float) val1 + (Integer) val2);
        } else if (val1 instanceof Float && val2 instanceof Float) {
          val1 = new Float((Float) val1 + (Float) val2);
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
        Object val2 = term(tk, itr, scope);
        if (val1 instanceof String) {
          throw new Exception("unsupported string operation: " + val1);
        } else if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 - (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Float) {
          val1 = new Float((Integer) val1 - (Float) val2);
        } else if (val1 instanceof Float && val2 instanceof Integer) {
          val1 = new Float((Float) val1 - (Integer) val2);
        } else if (val1 instanceof Float && val2 instanceof Float) {
          val1 = new Float((Float) val1 - (Float) val2);
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
  
  private Object term(Token tk, TokenIterator itr, Scope scope) throws Exception {
    Object val1 = exp(tk, itr, scope);
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
        Object val2 = exp(tk, itr, scope);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          val1 = new Integer((Integer) val1 * (Integer) val2);
        } else if (val1 instanceof Integer && val2 instanceof Float) {
          val1 = new Float((Integer) val1 * (Float) val2);
        } else if (val1 instanceof Float && val2 instanceof Integer) {
          val1 = new Float((Float) val1 * (Integer) val2);
        } else if (val1 instanceof Float && val2 instanceof Float) {
          val1 = new Float((Float) val1 * (Float) val2);
        } else {
          log.error("invalid MULT operation");
          throw new Exception("syntax error");
        }
      } else if (itr.peek().getType() == Token.DIV) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on DIV");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = exp(tk, itr, scope);
        if (val1 instanceof Integer && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          if ((Integer) val1 % (Integer) val2 == 0) {
            val1 = new Integer((Integer) val1 / (Integer) val2);
          } else {
            val1 = new Float(((Integer) val1).floatValue() / ((Integer) val2).floatValue());
          }
        } else if (val1 instanceof Integer && val2 instanceof Float) {
          if ((Float) val2 == 0f) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Float((Integer) val1 / (Float) val2);
        } else if (val1 instanceof Float && val2 instanceof Integer) {
          if ((Integer) val2 == 0) {
            throw new Exception("divide by 0 error");
          }
          val1 = new Float((Float) val1 / (Integer) val2);
        } else if (val1 instanceof Float && val2 instanceof Float) {
          val1 = new Float((Float) val1 / (Float) val2);
          if ((Float) val2 == 0f) {
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

  private Object exp(Token tk, TokenIterator itr, Scope scope) throws Exception {
    Object val1 = primary(tk, itr, scope);
    while (true) {
      if (!itr.hasNext()) {
        break;
      }
      if (itr.peek().getType() == Token.EXP) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on EXP");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = primary(tk, itr, scope);
        if (val1 instanceof Integer) {
          val1 = ((Integer) val1).floatValue();
        }
        if (val2 instanceof Integer) {
          val2 = ((Integer) val2).floatValue();
        }
        if (!(val1 instanceof Float) || !(val2 instanceof Float)) {
          log.error("exponentials must be real");
          throw new Exception("syntax error");
        }
        val1 = Math.pow((Float) val1, (Float) val2);
        val1 = ((Double) val1).floatValue();
      } else {
        break;
      }
    }
    return val1;
  }
  
  private Object primary(Token tk, TokenIterator itr, Scope scope) throws Exception {
    if (tk.getType() == Token.INTEGER || tk.getType() == Token.REAL || tk.getType() == Token.STRING) {
      return tk.getValue();
    }
    if (tk.getType() == Token.PLUS) {
      tk = itr.next();
      Object val = primary(tk, itr, scope);
      if (!(val instanceof Integer) && !(val instanceof Float)) {
        log.error("invalid unary plus");
        throw new Exception("syntax error");
      }
      return val;
    }
    if (tk.getType() == Token.MINUS) {
      tk = itr.next();
      Object val = primary(tk, itr, scope);
      if (val instanceof Integer) {
        return new Integer(-(Integer) val);
      } else if (val instanceof Float) {
        return new Float(-(Float) val);
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
        Object val = expression(tk, itr, scope);
        Symbol symbol = scope.getSymbol(symbolName);
        if (symbol != null && symbol.isConstant()) {
          throw new Exception("cannot write to a const: " + symbolName);
        }
        scope.setSymbol(symbolName, new Symbol(val));
      }
       
      Symbol symbol = scope.getSymbol(symbolName);
      if (symbol == null) {
        throw new Exception("uninitialized symbol: " + tk.getValue());
      }
      return symbol.getValue();
    }
    if (tk.getType() == Token.LPAREN) {
      tk = itr.next();
      Object val = expression(tk, itr, scope);
      if (!itr.hasNext()) {
        log.error("missing RPAREN (end of line)");
        throw new Exception("syntax error: unmatched lparen");
      }
      tk = itr.next();
      if (tk.getType() != Token.RPAREN) {
        log.error("missing RPAREN (unexpected token)");
        throw new Exception("syntax error: unmatched lparen");
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
      tk = itr.next();
      List<Object> params = new ArrayList<Object>();
      while (tk.getType() != Token.RPAREN) {
        Object val = expression(tk, itr, scope);
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
      return funcCaller.invokeFunction(funcName, params, scope);
    }
    throw new Exception("unsupported primary expression: " + tk);
  }
}
