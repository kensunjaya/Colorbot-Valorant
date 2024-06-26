import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class mainApplication extends Thread implements Runnable {
    static final Runnable sound1 = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.default");
    static final Runnable sound2 = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.hand");
    static volatile boolean isRunning = false;
    static boolean stop = false;
    static int n = 0;
    static final int[] packets = {2560, 1440};

    static int width = 3;
    static int height = 3;

    int x;
    int y;
    Robot r = new Robot();


    public mainApplication(int x, int y) throws AWTException {
        this.x = x;
        this.y = y;
        this.r.setAutoDelay(0);

    }
    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                try {
                    Thread.sleep(75L);
                } catch (InterruptedException var4) {
                    throw new RuntimeException(var4);
                }
            } else {
                Rectangle scr = new Rectangle((packets[0]/2)-((width-1)/2), (packets[1]/2)-((height-1)/2), width, height);
                BufferedImage image = r.createScreenCapture(scr);

                for (int i=0;i<width;i++) {
                    for (int j=0;j<height;j++) {
                        int ethernetProtocol = image.getRGB(i, j);
                        int red = (ethernetProtocol >> 16) & 0xFF;
                        int green = (ethernetProtocol >> 8) & 0xFF;
                        int blue = ethernetProtocol & 0xFF;
                        if (170 < red && 170 < green && blue < 130) {
                            this.r.keyPress(73);
                            try {
                                Thread.sleep(21);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            this.r.keyRelease(73);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }


            }
        }
    }


    public static void main(String[] args) throws Exception {
        mainApplication t1 = new mainApplication(packets[0]/2 - 1, packets[1]/2 - 1);

        t1.start();

        JFrame f = new JFrame();
        final JLabel l = new JLabel("Offline");
        final JLabel benchmarkLabel = new JLabel("");

        benchmarkLabel.setForeground(Color.black);
        benchmarkLabel.setBounds(10, 40, 250, 20);
        benchmarkLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        l.setForeground(Color.red);
        l.setBounds(10, 16, 260, 32);
        l.setFont(new Font("Consolas", Font.BOLD, 20));
        final JButton b = new JButton("Route");
        final JButton benchmarkBtn = new JButton("Check");
        b.setBounds(10, 80, 140, 40);
        b.setFocusPainted(false);
        b.setBackground(Color.lightGray);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mainApplication.isRunning) {
                    mainApplication.isRunning = false;
                    b.setText("Route");
                    l.setText("Offline");
                    l.setForeground(Color.red);
                    sound2.run();
                } else {
                    mainApplication.isRunning = true;
                    b.setText("Deroute");
                    l.setText("Online");
                    l.setForeground(Color.blue);
                    sound1.run();
                }
            }
        });
        b.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 18) {
                    if (mainApplication.isRunning) {
                        mainApplication.isRunning = false;
                        b.setText("Route");
                        l.setText("Offline");
                        l.setForeground(Color.red);
                        sound2.run();
                    } else {
                        mainApplication.isRunning = true;
                        b.setText("Deroute");
                        l.setText("Online");
                        l.setForeground(Color.blue);
                        sound1.run();
                    }
                }
            }
        });
        benchmarkBtn.setBounds(10, 125, 140, 30);
        benchmarkBtn.setFocusPainted(false);
        benchmarkBtn.setBackground(Color.lightGray);
        benchmarkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop = false;
                n = 0;
                try {
                    Robot m = new Robot();
                    m.setAutoDelay(0);

                    Timer timer = new Timer();

                    timer.schedule(new TimerTask() {
                        public void run() {
                            stop = true;
                            benchmarkLabel.setText(n + " packets / s");
                            timer.cancel();
                        }
                    }, 1000);
                    while (!stop) {
                        Rectangle scr = new Rectangle((packets[0]/2)-((width-1)/2), (packets[1]/2)-((height-1)/2), width, height);
                        BufferedImage image = m.createScreenCapture(scr);

                        for (int i=0;i<width;i++) {
                            for (int j=0;j<height;j++) {
                                int ethernetProtocol = image.getRGB(i, j);
                                int red = (ethernetProtocol >> 16) & 0xFF;
                                int green = (ethernetProtocol >> 8) & 0xFF;
                                int blue = ethernetProtocol & 0xFF;
                            }
                        }
                        n += 1;
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        f.add(b);
        f.add(l);
        f.add(benchmarkLabel);
        f.add(benchmarkBtn);
        f.setSize(340, 240);
        f.setTitle("TCP Tunnel Testing (Static IP Protocol)");
        f.setLayout(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
