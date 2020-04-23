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
    Option opt = new Option("t", "content-type", true, "content type of the import (one of: csv | html | fred | manual [default])");
    opt.setArgName("type");
    options.addOption(opt);
    opt = new Option("m", "format", true, "format when importing csv content (e.g. date:yyyy-MM-dd,scope-id,source-id,value)");
    opt.setArgName("format");
    options.addOption(opt);
    opt = new Option("s", "source-id", true, "source id");
    opt.setArgName("id");
    options.addOption(opt);
    opt = new Option("p", "filepath", true, "file path");
    opt.setArgName("path");
    options.addOption(opt);
    opt = new Option("u", "url", true, "url");
    opt.setArgName("url");
    options.addOption(opt);
    opt = new Option("f", "force", false, "force overwrite");
    options.addOption(opt);
    opt = new Option("i", "id", true, "id to store in database (required)");
    opt.setArgName("id");
    opt.setRequired(true);
    options.addOption(opt);
    opt = new Option("c", "class", true, "class which tags the html element storing the data point (html imports only)");
    opt.setArgName("class");
    options.addOption(opt);
    opt = new Option("v", "value", true, "value of the data point (manual imports only)");
    opt.setArgName("value");
    options.addOption(opt);
    opt = new Option("n", "now", true, "use today's date (time) during imports, if otherwise not available");
    opt.setArgName("value");
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
    System.out.println();
    System.out.println("examples:");
    System.out.println("dtrader import --content-type csv --file /c/ng-static.txt --format date:yyyyMMdd,,source-id:CDN10,value -id CDN10");
    System.out.println("dtrader import --content-type csv --file /c/ng-static.txt --format date:yyyyMMdd,scope-id:^GSPC.TXT,source-id:EPS,value -id GSPC:EPS");
  }
}
