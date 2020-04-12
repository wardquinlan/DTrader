package daytrader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Tokenizer {
  private static Log log = LogFactory.getFactory().getInstance(Tokenizer.class);
  
  public List<Token> getTokens(String path) {
    LookAheadReader rdr = null;
    List<Token> list = new ArrayList<Token>();
    try {
      rdr = new LookAheadReader(new FileInputStream(path));
      while (true) {
        int val = rdr.read();
        if (val == -1) {
          break;
        }
        if (Character.isLetter(val)) {
          Token tk = new Token(Token.SYMBOL);
          StringBuffer sb = new StringBuffer();
          sb.append((char) val);
          while (Character.isLetter(rdr.peek()) || Character.isDigit(rdr.peek()) || rdr.peek() == '_') {
            sb.append((char) rdr.read());
          }
          tk.setValue(sb.toString());
          list.add(tk);
        } else if (val == '.' || Character.isDigit(val)) {
          Token tk = new Token(Token.NUMBER);
          StringBuffer sb = new StringBuffer();
          sb.append((char) val);
          while (rdr.peek() == '.' || Character.isDigit(rdr.peek())) {
            if (rdr.peek() == '.' && sb.toString().contains(".")) {
              throw new Exception("misformatted value: " + sb);
            }
            sb.append((char) rdr.read());
          }
          if (sb.toString().equals(".")) {
            throw new Exception("misformatted value: " + sb);
          }
          tk.setValue(sb.toString());
          list.add(tk);
        } else if (val == '"') {
          Token tk = new Token(Token.STRING);
          StringBuffer sb = new StringBuffer();
          while (true) {
            if (rdr.peek() == '"') {
              rdr.read();
              break;
            }
            if (rdr.peek() == -1) {
              throw new Exception("unterminated string: " + sb);
            }
            if (rdr.peek() == '\\') {
              rdr.read();
              if (rdr.peek() == -1) {
                throw new Exception("unterminated string: " + sb);
              }
            }
            sb.append((char) rdr.read());
          }
          tk.setValue(sb.toString());
          list.add(tk);
        } else if (val == '(') {
          Token tk = new Token(Token.LPAREN);
          list.add(tk);
        } else if (val == ')') {
          Token tk = new Token(Token.RPAREN);
          list.add(tk);
        } else if (val == '{') {
          Token tk = new Token(Token.LBRACE);
          list.add(tk);
        } else if (val == '}') {
          Token tk = new Token(Token.RBRACE);
          list.add(tk);
        } else if (val == '=' && rdr.peek() == '=') {
          Token tk = new Token(Token.EQ);
          list.add(tk);
        } else if (val == '=') {
          Token tk = new Token(Token.ASSIGN);
          list.add(tk);
        }
      }
    } catch(Exception e) {
      log.error(e);
    } finally {
      if (rdr != null) {
        try {
          rdr.close();
        } catch(IOException e) {
          log.warn("cannot close reader", e);
        }
      }
    }
    return list;
  }
}
