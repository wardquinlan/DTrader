package dtrader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DTrader {
  private static Log log = LogFactory.getFactory().getInstance(DTrader.class);
  private static String version = "0.70";
  private HelpFormatter formatter = new HelpFormatter();
  
  public DTrader(String[] args) throws Exception {
    loadProperties();
    dispatch(args);
  }

  private void dispatch(String[] args) throws Exception {
    if (args.length == 0) {
      usage();
    }
    String cmd = args[0];
    args = ArrayUtils.remove(args, 0);
    if ("import".equals(cmd)) {
      Command command = new ImportCommand();
      command.execute(args);
    } else if ("visual".equals(cmd)) {
      runVisual(args);
    } else {
      usage();
    }
  }

  private void usage() {
    System.out.println("dtrader version " + version);
    System.out.println("-------------------------------------------\n");
    System.out.println("usage:\n");
    System.out.println("dtrader import - imports data from various sources");
    System.out.println("dtrader visual - runs dtrader in visual mode");
    System.exit(1);
  }
  
  private void usageVisual(String[] args) {
    Options options = new Options();
    Option opt = new Option("n", "name", true, "chart name");
    opt.setArgName("chart-name");
    options.addOption(opt);
    opt = new Option("s", "script", true, "script file");
    opt.setArgName("script-name");
    options.addOption(opt);
    formatter.printHelp("dtrader chart", options);
  }
  
  private void usageImport(String[] args) {
    Options options = new Options();
    //Option opt = new Option("
    options = new Options();
    Option opt = new Option("s", "source", true, "source (one of: qt-tpl | qt-db | fred | html | manual)");
    opt.setArgName("source");
    
    options.addOption(opt);
    opt = new Option("i", "input-id", true, "input id");
    opt.setArgName("id");
    System.out.println(opt.isRequired());
    options.addOption(opt);
    opt = new Option("o", "output-id", true, "output id");
    opt.setArgName("id");
    options.addOption(opt);
    opt = new Option("p", "scope", true, "scope");
    opt.setArgName("scope");
    options.addOption(opt);
    opt = new Option("v", "value", true, "value (for manual imports)");
    opt.setArgName("value");
    options.addOption(opt);
    opt = new Option("u", "url", true, "url (for html imports)");
    opt.setArgName("value");
    options.addOption(opt);
    opt = new Option("c", "class", true, "class name (for html imports)");
    opt.setArgName("value");
    options.addOption(opt);
    opt = new Option("d", "date", true, "date (for html and manul imports)");
    opt.setArgName("yyyy-mm-dd");
    options.addOption(opt);
    opt = new Option("t", "time", true, "time (for html and manul imports)");
    opt.setArgName("hh:mm");
    options.addOption(opt);
    opt = new Option("f", "force", false, "force overwrites");
    options.addOption(opt);
    //formatter.printHelp("dtrader import", options);    
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      System.out.println(cmd.hasOption('i'));
      System.out.println(cmd.hasOption("input-id"));
    } catch(ParseException e) {
      log.error(e);
    }
    
  }

  private void runVisual(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DTrader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new ChartPanel();
        panel.setBackground(Color.ORANGE);
        frame.getContentPane().add(panel);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(size);
        frame.setVisible(true);          
      }
    });    
  }
  
  private void runImport(String[] args) {
    
    usageImport(args);
  }
  
  private void loadProperties() {
    ClassLoader cl = ClassLoader.getSystemClassLoader();  
    try {
      InputStream is = cl.getResourceAsStream("dtrader.properties");
      if (is == null) {
        log.error("cannot load properties");
        System.exit(1);
      }
      System.getProperties().load(is);    
    } catch(IOException e) {
      log.error("cannot load properties", e);
      System.exit(1);
    }
  }
  
  public static void main(String[] args) {
    ArrayList<String> argList = new ArrayList<String>(args.length);
    for (int i = 0; i < args.length; i++) {
      argList.add(args[i]);
    }

    try {
      new DTrader(args);
      new SeriesDAO();
    } catch(Exception e) {
      log.error(e);
    }
    
    try {
      Tokenizer tokenizer = new Tokenizer("samples/test1.dt");
      TokenIterator itr = tokenizer.tokenize();
      if (itr.hasNext()) {
        Map<String, Symbol> symbolTable = new HashMap<String, Symbol>();
        Parser parser = new Parser(symbolTable);
        Token tk = itr.next();
        parser.parse(tk, itr);
      }
      int n = 5;
    } catch(Exception e) {
      log.error(e);
      System.err.println(e.getMessage());
    }
  }
}
