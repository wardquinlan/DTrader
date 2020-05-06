package dtrader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScriptCommand extends Command {
  private static Log log = LogFactory.getFactory().getInstance(ScriptCommand.class);
      
  @Override
  public void execute(String[] args) {
    Options options = new Options();
    Option opt = new Option("p", "filepath", true, "path of script to execute");
    opt.setArgName("path");
    opt.setRequired(true);
    options.addOption(opt);
    try {
      CommandLineParser cmdParser = new DefaultParser();
      CommandLine cmd = cmdParser.parse(options, args);
      Tokenizer tokenizer = new Tokenizer(cmd.getOptionValue("filepath"));
      TokenIterator itr = tokenizer.tokenize();
      if (!itr.hasNext()) {
        throw new Exception("empty script file");
      }
  
      Parser parser = new Parser();
      Token tk = itr.next();
      parser.parse(tk, itr);
    } catch(ParseException e) {
      super.usage("script", options);
      System.exit(1);
    } catch(Exception e) {
      log.error(e);
      System.exit(1);
    }
  }
}
