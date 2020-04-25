package dtrader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VisualCommand extends Command {
  private static Log log = LogFactory.getFactory().getInstance(VisualCommand.class);
      
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
      Scope root = parser.parse(tk, itr);
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JFrame frame = new JFrame("DTrader");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          JPanel panel = new ChartPanel();
          Object obj = root.getProperty("chart.background");
          Color colorBackground = new Color(0xcccccc);
          if (obj != null && obj instanceof Long) {
            colorBackground = new Color(((Long) obj).intValue());
          } else {
            log.warn("chart.background not set, using default background color");
          } 
          panel.setBackground(colorBackground);
          JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
          tabbedPane.addTab("Tab 1", panel);
          tabbedPane.addTab("Tab 2", new JPanel());
          JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, new JPanel());
          
          frame.getContentPane().add(splitPane);
          Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
          frame.setSize(size);
          frame.setVisible(true);
          splitPane.setDividerLocation(0.75);
        }
      });
    } catch(ParseException e) {
      usage(options);
      System.exit(1);
    } catch(Exception e) {
      log.error(e);
      System.exit(1);
    }
  }
  
  private void usage(Options options) {
    formatter.printHelp("dtrader visual", options);
  }
}
