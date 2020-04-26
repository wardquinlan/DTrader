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
      Command command = new VisualCommand();
      command.execute(args);
    } else if ("script".equals(cmd)) {
      Command command = new ScriptCommand();
      command.execute(args);
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
    System.out.println("dtrader script - runs a dtrader script");
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
    try {
      new DTrader(args);
      new SeriesDAO();
    } catch(Exception e) {
      log.error(e);
    }
  }
}
