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
    Scope scopeCurr = parseScope(tk, itr, scope);
    return new Chart(name, scopeCurr);
  }
  
  private Scope parseScope(Token tk, TokenIterator itr, Scope scope) throws Exception {
    if (!itr.hasNext()) {
      log.error("invalid scope declaration");
      throw new Exception("syntax error");
    }
    Scope scopeCurr = new Scope(scope);
    tk = itr.next();
    while (true) {
      Statement statement = parseStatement(tk, itr, scopeCurr);
      if (!itr.hasNext()) {
        log.error("unexpected end of scope");
        throw new Exception("syntax error");
      }
      scope.getStatements().add(statement);
      tk = itr.next();
      if (tk.getType() == Token.RBRACE) {
        break;
      }
    }
    return scopeCurr;
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
        Object val2 = term(tk, itr, scope);
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
      } else if (itr.peek().getType() == Token.DIV) {
        itr.next();
        if (!itr.hasNext()) {
          log.error("missing RHS on DIV");
          throw new Exception("syntax error");
        }
        tk = itr.next();
        Object val2 = exp(tk, itr, scope);
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
      if (!(val instanceof Long) && !(val instanceof Double)) {
        log.error("invalid unary plus");
        throw new Exception("syntax error");
      }
      return val;
    }
    if (tk.getType() == Token.MINUS) {
      tk = itr.next();
      Object val = primary(tk, itr, scope);
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
