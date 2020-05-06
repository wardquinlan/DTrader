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

  private void paintBackground(Graphics g) {
    setBackground(new Color(0xffff00));
  }
  
  private void paintTitle(Graphics g) {
    int widthPanel = getWidth();
    FontMetrics m = g.getFontMetrics(g.getFont());
    int heightFont = m.getHeight();
    int widthText = m.stringWidth("Chart Title");
    g.drawString("Chart Title", (widthPanel - widthText) / 2, heightFont);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintBackground(g);
    paintTitle(g);
  }
}
