package daytrader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DayTrader {
  private static Log log = LogFactory.getFactory().getInstance(DayTrader.class);
  public static void main(String[] args) {
    Tokenizer tokenizer = new Tokenizer();
    tokenizer.getTokens("/home/ward/workspace/daytrader/samples/samp1.dt");
  }
}
