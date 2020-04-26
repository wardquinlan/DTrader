package dtrader;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public abstract class Command {
  protected HelpFormatter formatter = new HelpFormatter();

  public abstract void execute(String[] args);
  
  protected void usage(String command, Options options) {
    formatter.printHelp("dtrader " + command, options);
  }
}
