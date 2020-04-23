package dtrader;

import org.apache.commons.cli.HelpFormatter;

public abstract class Command {
  protected HelpFormatter formatter = new HelpFormatter();

  public abstract void execute(String[] args);
}
