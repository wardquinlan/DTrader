package daytrader;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DayTrader {
  private static Log log = LogFactory.getFactory().getInstance(DayTrader.class);
  public static void main(String[] args) {
    try {
      Tokenizer tokenizer = new Tokenizer("samples/samp2.dt");
      List<Token> tokens = tokenizer.tokenize();
      for (Token tk: tokens) {
        System.out.println(tk);
      }
    } catch(Exception e) {
      log.error(e);
    }
  }
}
