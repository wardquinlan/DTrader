package daytrader;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DayTrader {
  private static Log log = LogFactory.getFactory().getInstance(DayTrader.class);
  public static void main(String[] args) {
    LookAheadReader rdr = null;
    try {
      rdr = new LookAheadReader(new FileInputStream("/home/ward/workspace/daytrader/samples/samp1.dt"));
      while (true) {
        int ch = rdr.read();
        if (ch == -1) {
          break;
        }
        System.out.print((char) ch);
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
  }
}
