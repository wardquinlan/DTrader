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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DTrader {
  private static Log log = LogFactory.getFactory().getInstance(DTrader.class);
  private static String version = "0.70";
  
  public DTrader(List<String> args) {
    loadProperties();
    dispatch(args);
  }

  private void dispatch(List<String> argList) {
    if (argList.size() == 0) {
      usage();
    } else if ("chart".equals((String) argList.get(0))) {
      argList.remove(0);
      chart(argList);
    } else {
      usage();
    }
  }

  private void chart(List<String> argList) {
    String chartName = null;
    if (argList.size() == 0) {
      log.error("no arguments");
      usage();
    }
    String param = argList.remove(0);
    if ("--chart-name".equals(param)) {
      if (argList.size() == 0) {
        log.error("missing --chart-name argument");
        usage();
      }
      chartName = argList.remove(0);
      if (argList.size() == 0) {
        log.error("missing file");
        usage();
      }
      param = argList.remove(0);
    }
    if (argList.size() > 0) {
      log.error("too many arguments");
      usage();
    }
    String fileName = param;
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
  
  private void usage() {
    Options options = new Options();
    Option opt = new Option("s", "source", true, "source (one of: qt-tpl | qt-db | fred | html | manual)");
    opt.setArgName("source");
    options.addOption(opt);
    opt = new Option("i", "input-id", true, "input id");
    opt.setArgName("id");
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
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("dtrader import", options);    
    
    
    System.out.println("dtrader version " + version);
    System.out.println("-------------------------------------------\n");
    System.out.println("usage:\n");
    System.out.println("dtrader import --source quote-template|quote-db|fred --import-id import-id [--import-ticker ticker] [--force] id");
    System.out.println("  imports data from source using given id (and optional ticker for quote imports), with optional force overwrites\n");
    System.out.println("dtrader update\n");
    System.out.println("dtrader chart [--chart-name name] config-file.dt");
    System.out.println("  loads dtrader in gaphical mode and renders 'name' (defaults to first chart found)\n");
    System.exit(1);
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
    
    new DTrader(argList);

    try {
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
