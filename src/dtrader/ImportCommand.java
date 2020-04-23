package dtrader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImportCommand extends Command {
  private static Log log = LogFactory.getFactory().getInstance(ImportCommand.class);
      
  @Override
  public void execute(String[] args) {
    Options options = new Options();
    Option opt = new Option(null, "content-type", true, "content type of the import (one of: csv | html | fred | manual [default])");
    opt.setArgName("type");
    options.addOption(opt);
    opt = new Option(null, "csv-format", true, "csv format (e.g. D:yyyy-MM-dd,scope,id,value)");
    opt.setArgName("format");
    options.addOption(opt);
    opt = new Option(null, "source-id", true, "source id");
    opt.setArgName("id");
    options.addOption(opt);
    opt = new Option(null, "file", true, "file");
    opt.setArgName("path");
    options.addOption(opt);
    opt = new Option(null, "url", true, "url");
    opt.setArgName("url");
    options.addOption(opt);
    
    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);
      System.out.println(cmd.hasOption("content-type"));
    } catch(ParseException e) {
      usage(options);
      System.exit(1);
    }
  }

  private void usage(Options options) {
    formatter.printHelp("dtrader import", options);
  }
}
