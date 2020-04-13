package daytrader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DayTrader {
  private static Log log = LogFactory.getFactory().getInstance(DayTrader.class);
  public static void main(String[] args) {
    try {
      Tokenizer tokenizer = new Tokenizer("samples/samp2.dt");
      List<Token> tokens = tokenizer.tokenize();
      Map<String, Object> symbolTable = new HashMap<String, Object>();
      Parser parser = new Parser(symbolTable);
      parser.parse(tokens.iterator());
      int n = 5;
    } catch(Exception e) {
      log.error(e);
    }
  }
}
