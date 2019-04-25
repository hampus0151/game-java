import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;

public class game extends Canvas implements Runnable{
    // Bounding boxes för målet och för tangenterna
    Rectangle target;
    Rectangle striker;
    // Slumpgenerator för att slumpa ut nya mål
    Random R;

    BufferedImage targetImg;
    BufferedImage strikerImg;

    int targetSpeed;
    int strikerSpeed = 10;


    BufferStrategy bs;
    //bestämmer storlek på spelplanen
    int width = 800;
    int height = 600;
    Thread thread;
    boolean running = false;
    //int speed;
    int hits = 0;

    public game() {
        R = new Random();
        // skapar target på lämplig plats i x led
        target = new Rectangle(R.nextInt(width-50),10, 60,45);

        //bestämmer att spelaren var spelaren börjar
        striker = new Rectangle(width, height,40,40);
        striker.x = 502;
        striker.y = 522;


        try {
            targetImg = ImageIO.read(new File("picture.png"));
            strikerImg = ImageIO.read(new File("kanon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSize(width, height);
        JFrame frame = new JFrame("Grafik med kollisioner");
        frame.add(this);
        // Lägg till en lyssnare för tangentbordet för att kunna styra spelaren
        this.addKeyListener(new  game.KL());



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Start, stop och run är metoder som kommer från Runnable. Det gör att vi kan starta en ny processortråd
     * som kör runmetoden där man kan rita upp skärmen
     */
    public synchronized void start() {
        //man får skriva in hastighet för target
        targetSpeed = Integer.parseInt(JOptionPane.showInputDialog(null,"Välj svårighetsgrad mellan 1-10. dubbeltryck på OK"));
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* bestämmer att fps eller ups 60. Delta anger i hur nära vi är en ny uppdatering.
       När delta blir 1 är det dags att rita igen. delta nollställs inte eftersom man måste göra flera uppdateringar efter varandra.
       Här ligger update och render i samma tidssteg. Det går att separera dessa. Egentligen kan vi rita ut hur fort som
       helst (lägga render utanför while(delta>1)) Det viktiga är att update anropas med konstant hastighet eftersom det
       är den som simulerar tiden i animeringar.
     */
    public void run() {
        double ns = 1000000000.0 / 60.0;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1) {
                // Uppdatera koordinaterna
                update();
                // Rita ut bilden med updaterad data
                render();
                delta--;
            }
        }
        stop();
    }

    /**
     * Eftersom vi inte längre behöver paint och repaint döps metoden om till render
     */
    public void render() {
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        // Rita ut den nya bilden
        draw(g);
        g.dispose();
        bs.show();
    }

    public void draw(Graphics g) {
        // rensa skärmen
        g.setColor(new Color(0xFFFFFF));
        g.fillRect(0,0,width,height);
        // Rita ut biolderna men använd motsvarande rektangel för att få placering och storlek
        g.drawImage(targetImg, target.x,target.y,target.width,target.height,null);
        g.drawImage(strikerImg, striker.x,striker.y,striker.width,striker.height,null);
    }

    // Uppdatera inget i detta exempel. target skulle kunna röra sig...
    private void update() {
        target.y = target.y+targetSpeed;
        if (striker.intersects(target)) {
            target = new Rectangle(R.nextInt(width - 50), 10, 60, 45);
            hits++;
        }
        if (target.y>600){
            JOptionPane.showMessageDialog(null,"du tog "+hits+ " ailiens! tryck OK för att avsluta");
            System.exit(0);
        }
    }


    public static void main(String[] args) {
        game minGrafik = new game();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                minGrafik.setVisible(true);
            }
        });
        minGrafik.start();
    }


     //keyListener lyssnar efter tangentbordet
     // KL står för keylistner
    private class KL implements KeyListener {


        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            //39 och 37 är höger och vänster piltangen lägger till att dom ska kontrollera striker med hastigheten strikerspeed

            int key = e.getKeyCode();
            if (key == 39) {
                striker.x = striker.x+strikerSpeed;
            }
            if (key == 37) {
                striker.x = striker.x-strikerSpeed;
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
