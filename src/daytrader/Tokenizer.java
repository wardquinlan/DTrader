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
        int ch = rdr.read();
        if (ch == -1) {
          break;
        }
        if (Character.isLetter(ch)) {
          Token tk = new Token(Token.SYMBOL);
          StringBuffer sb = new StringBuffer();
          while (Character.isLetter(rdr.peek()) || Character.isDigit(rdr.peek()) || rdr.peek() == '_') {
            sb.append(rdr.read());
          }
          tk.setValue(sb.toString());
          list.add(tk);
          System.out.println(tk.getValue());
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
