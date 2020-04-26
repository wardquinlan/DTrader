package dtrader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ChartPanel extends JPanel {
  private Chart chart;
  
  public ChartPanel(Chart chart) {
    this.chart = chart;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    System.out.println(g.getFont().getName());
    System.out.println(g.getFont().getStyle());
    System.out.println(g.getFont().getSize());
    long l = (Long) chart.getScope().getProperty("font.size");
    
    Font font = new Font(g.getFont().getName(), g.getFont().getStyle(), (int) l);
    g.setFont(font);
    g.setColor(Color.BLUE);
    g.drawLine(30, 50, 70, 90);
    //g.drawRect(80, 80, 200, 100);
    
    g.fillRect(80, 80, 200, 100);
    g.drawString("Hello there", 30, 50);
  }
}
