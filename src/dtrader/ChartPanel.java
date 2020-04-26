package dtrader;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChartPanel extends JPanel {
  private static Log log = LogFactory.getFactory().getInstance(ChartPanel.class);
  private Chart chart;
  
  public ChartPanel(Chart chart) {
    this.chart = chart;
  }
  
  private void paintTitle(Graphics g) {
    if (chart.getScope().getProperty("chart.title") == null) {
      return;
    }
    if (!(chart.getScope().getProperty("chart.title") instanceof String)) {
      log.warn("property chart.title not a string");
      return;
    }
    Font fontOrig = g.getFont();
    Color colorOrig = g.getColor();
    if (chart.getScope().getProperty("chart.title.color") != null) {
      if (chart.getScope().getProperty("chart.title.color") instanceof Integer) { 
        g.setColor(new Color((Integer) chart.getScope().getProperty("chart.title.color")));
      } else {
        log.warn("property chart.title.color not an integer");
      }
    }
    if (chart.getScope().getProperty("chart.title.size") != null) {
      if (chart.getScope().getProperty("chart.title.size") instanceof Integer) {
        int size = (Integer) chart.getScope().getProperty("chart.title.size");
        Font font = new Font(g.getFont().getName(), g.getFont().getStyle(), size);
        g.setFont(font);
      } else {
        log.warn("property chart.title.size not an integer");
      }
    }
    int widthPanel = getWidth();
    FontMetrics m = g.getFontMetrics(g.getFont());
    int heightFont = m.getHeight();
    int widthText = m.stringWidth((String) chart.getScope().getProperty("chart.title"));
    
    g.drawString((String) chart.getScope().getProperty("chart.title"), (widthPanel - widthText) / 2, heightFont);
    g.setFont(fontOrig);
    g.setColor(colorOrig);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintTitle(g);
  }
}
