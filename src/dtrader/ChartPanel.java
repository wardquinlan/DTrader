package dtrader;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChartPanel extends JPanel {
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.BLUE);
    g.drawLine(30, 50, 70, 90);
    //g.drawRect(80, 80, 200, 100);
    
    g.fillRect(80, 80, 200, 100);
    g.drawString("Hello there", 30, 50);
  }
}
