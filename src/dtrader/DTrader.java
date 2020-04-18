package dtrader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DTrader {
  private static Log log = LogFactory.getFactory().getInstance(DTrader.class);
  private static String version = "0.70";
  
  public DTrader(List<String> args) {
    loadProperties();
    Integer ret = dispatch(args);
    System.exit(ret);
  }

  private Integer dispatch(List<String> argList) {
    if (argList.size() == 0) {
      return usage();
    } else if ("chart".equals((String) argList.get(0))) {
      argList.remove(0);
      return chart(argList);
    } else {
      return usage();
    }
  }

  private Integer chart(List<String> argList) {
    String chart = null;
    if (argList.size() == 0) {
      log.error("no arguments");
      return usage();
    }
    String param = argList.remove(0);
    if ("--chart-name".equals(param)) {
      if (argList.size() == 0) {
        log.error("missing --chart-name argument");
        return usage();
      }
      chart = argList.remove(0);
      if (argList.size() == 0) {
        log.error("missing file");
        return usage();
      }
      param = argList.remove(0);
    }
    if (argList.size() > 0) {
      log.error("too many arguments");
      return usage();
    }
    String file = param;
    System.out.println(chart + "," + param);
    return 0;
  }
  
  private Integer usage() {
    System.out.println("dtrader version " + version);
    System.out.println("-------------------------------------------\n");
    System.out.println("usage:\n");
    System.out.println("dtrader chart [--chart-name name] config-file.dt");
    System.out.println("  loads dtrader in gaphical mode and renders 'name' (defaults to first chart found)\n");
    return 1;
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

    /*
    try {
      Tokenizer tokenizer = new Tokenizer("samples/test1.dt");
      List<Token> tokens = tokenizer.tokenize();
      Map<String, Object> symbolTable = new HashMap<String, Object>();
      Parser parser = new Parser(symbolTable);
      parser.parse(tokens.iterator());
      int n = 5;
    } catch(Exception e) {
      log.error(e);
      System.err.println(e.getMessage());
    }
    */
  }
}
