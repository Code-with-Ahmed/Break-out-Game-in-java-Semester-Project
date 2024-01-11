package breakout.ball;

import breakout.ball.MapGenerator;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.JPanel;

public class Gameplay extends JPanel implements ActionListener, KeyListener {

    private boolean play = false;
    private int score = 0;
    private int totalbricks = 36;
    private Color ballColor;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;
    private MapGenerator map;

    private boolean isPaused = false;

    public Gameplay() {
        map = new MapGenerator(4, 9);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        ballColor = getRandomColor();

        // Add a MouseAdapter to handle pause/resume on mouse click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
                    togglePause();
                }
            }
        });

        timer.start();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        map.draw((Graphics2D) g);

        g.setColor(Color.red);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(681, 0, 3, 592);

        g.setColor(Color.green);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        g.setColor(ballColor);
        g.fillRect(playerX, 550, 100, 8);

        g.setColor(ballColor);
        g.fillOval(ballposX, ballposY, 20, 20);

        if (totalbricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won", 260, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }

        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 160;
                ballposY = 320;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                score = 0;
                totalbricks = 36;
                map = new MapGenerator(4, 9);

                repaint();
            }
        }

        // Add space key handling for pause/resume
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            togglePause();
        }
    }

    public void moveLeft() {
        play = true;
        playerX -= 15;
    }

    public void moveRight() {
        play = true;
        playerX += 15;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play && !isPaused) {
            int distance = ballposX - playerX;
            //It ensures that the paddle moves a maximum of 5 pixels in each update.
            int step = Math.min(5, Math.abs(distance));

            //distance is positive, the paddle moves to the right.
            // distance is negative, the paddle moves to the left.
            if (distance > 0) {
                playerX += step;
            } else if (distance < 0) {
                playerX -= step;
            }

            if (playerX < 0) {
                playerX = 0;
            } else if (playerX > getWidth() - 100) {
                playerX = getWidth() - 100;
            }
            //checks the ball interacts with paddle
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            } else if (new Rectangle(ballposX, ballposY, 20, 20)
                    .intersects(new Rectangle(playerX + 70, 550, 100, 8))) {
                ballYdir = -ballYdir;
                ballXdir = ballXdir + 1;
            } else if (new Rectangle(ballposX, ballposY, 20, 20)
                    .intersects(new Rectangle(playerX + 30, 550, 110, 8))) {
                ballYdir = -ballYdir;
            }

            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            if (map.getBrickHits(i, j) == 0) {
                                map.incrementBrickHits(i, j);
                                map.setBrickValue(MapGenerator.CRACKED_BRICK, i, j);
                            } else if (map.getBrickHits(i, j) == 1) {
                                map.setBrickValue(MapGenerator.DISAPPEARED_BRICK, i, j);
                                score += 5;
                                totalbricks--;
                            }

                            ballYdir = -ballYdir;
                            ballColor = getRandomColor();
                            break A;
                        }
                    }
                }
            }
            ballposX += ballXdir;
            ballposY += ballYdir;

            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }
            repaint();
        }
    }

    private Color getRandomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);
        return new Color(red, green, blue);
    }
}
