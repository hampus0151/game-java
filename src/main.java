import javax.swing.*;
import java.awt.*;

public class Grafik extends Canvas {
    int x,y;


    public Grafik() {
        setSize(800,600);
        JFrame frame = new JFrame("main");
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        addKeyListener();
    }

    public void draw(Graphics g) {
        drawBox(10,10, g);
        drawBox(30,20,g);
        g.setColor(new Color(0x5500FFFF));
        g.fillOval(20,20,10,15);
    }

    private void wBox(int x, int y, Graphics g) {
        g.setColor(new Color(0x55FF00FF));
        g.drawRect(x,y,300,200);
    }

    public static void main(String[] args) {
    }
}