package dtrader;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChartPanel extends JPanel {
  public void paint(Graphics g) {
    g.drawLine(30, 50, 70, 90);
    g.drawRect(80, 80, 200, 100);
    g.drawString("Hello there", 30, 50);
  }
}
