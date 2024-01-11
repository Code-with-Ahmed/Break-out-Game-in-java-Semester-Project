package breakout.ball;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.QuadCurve2D;
import java.util.Random;

public class MapGenerator {

    public int map[][];
    public int brickWidth;
    public int brickHeight;
    int[][] brickHits;
    private int rows;
    private int columns;
    public static final int REGULAR_BRICK = 1;
    public static final int CRACKED_BRICK = 2;
    public static final int DISAPPEARED_BRICK = 0;
    
     private long[][] brickSeeds;  // Store unique seeds for each brick
    
    public MapGenerator(int row, int col) {
        rows = row;
        columns = col;
        map = new int[row][col];
        brickHits = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
                brickHits[i][j] = 0;
            }
            brickSeeds = new long[row][col];
        initializeBrickSeeds();
        }
        
        
        
        // Adjust the brick size based on the desired size
        brickWidth = 540 / col;  // Adjust the numerator to increase brick width
        brickHeight = 150 / row; // Adjust the numerator to increase brick height

        // Load the cracked brick image
//        ImageIcon icon = new ImageIcon("C:\\Users\\LENOVO\\Desktop\\Brick.jpg");
//        crackedBrickImage = icon.getImage();
    }
    
    private void initializeBrickSeeds() {
        
        Random random = new Random();
        
        
        /*Each brick in the game is associated with a unique seed value. These seeds are used in the drawSquigglyLines 
          function to ensure that the pattern of cracks on each brick is unique and not the same for all bricks.*/
        // Initialize unique seeds for each brick
        
        for (int i = 0; i < brickSeeds.length; i++) {
            for (int j = 0; j < brickSeeds[0].length; j++) {
                brickSeeds[i][j] = random.nextLong();  //yahan pa nextLong random ka 1 meethod ha jo 1 long value assign krta
            }                                          //hai har ak brick ko.....
        }
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
        for (int j = 0; j < map[0].length; j++) {
            if (map[i][j] > 0) {
                // Draw the brick in white
                g.setColor(Color.white);
                g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                g.setStroke(new BasicStroke(3));
                g.setColor(Color.black);
                g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                // If it's a cracked brick and has been hit once, draw squiggly lines in black
                if (map[i][j] == CRACKED_BRICK && brickHits[i][j] == 1) {
                     drawSquigglyLines(g, j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight, brickSeeds[i][j], brickHits[i][j]);
                }else if (brickHits[i][j] == 2) {
                    continue;  // Skip drawing disappeared bricks
                } else {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
            }
        }
    }
    

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    public int getBrickHits(int row, int col) {
        return brickHits[row][col];
    }

    public void incrementBrickHits(int row, int col) {
        brickHits[row][col]++;
    }
    
    
private void drawSquigglyLines(Graphics2D g, int x, int y, int width, int height, long seed,  int hits) {
   Random random = new Random(seed);

    // Set the stroke to a variable-width stroke to draw lines with varying thickness
    g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

    // Define the number of crack lines and their maximum length
    int numLines = 15;
    int Length = 19;

    //loop to draw the lines
    for (int i = 0; i < numLines; i++) {
        int startX = x + width / 2;
        int startY = y + height / 2;
        
        // Generate a random angle for the crack line based on hits
        double angle = getAdjustedAngle(random,hits);
        
        // Generate a random length for the crack line
        int length = random.nextInt(Length + 1);

        // Calculate the end coordinates of the crack line
        /*maxLength is calculated as the minimum of 30 and the width of the brick (Math.min(30, width)).
        This ensures that maxLength will be at most 30 but can be smaller if the width of the brick is 
        less than 30.
        maxVerticalLength ka liya b same kaam kiya ha brick ki height ko dkhty va*/
        
        
        int maxLength = Math.min(30, width); // Limit width to 30
        int maxVerticalLength = Math.min(15, height); // Limit height to 15

        //Using Trigonometric Functions to Calculate End Coordinates
        /*The use of Math.cos(angle) and Math.sin(angle) calculates the horizontal and vertical 
        components of the crack line based on the randomly generated angle. This ensures that the crack
        lines have a natural appearance and follow a squiggly pattern.*/
        
        
        int endX = startX + (int) (maxLength * Math.cos(angle));
        int endY = startY + (int) (maxVerticalLength * Math.sin(angle));

        // Vary the thickness of the crack line
        int thickness = random.nextInt(3);

        

        g.setColor(Color.black);
        g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        //darwline method draws the lines from staring to ending coordinaties....
        g.drawLine(startX, startY, endX, endY);
    }
}
private double getAdjustedAngle(Random random, int hits) {
    // Adjust the angle based on the number of hits
    double baseAngle = random.nextDouble() * Math.PI * 2;

    // Introduce randomness to the angle adjustment for squiggly lines
    double randomAngleAdjustment = random.nextDouble() * Math.PI / 40; // Adjust the divisor for more or less squiggly lines

    // You can adjust the angle based on hits
    double angleAdjustment = hits * (Math.PI / 6); // Example: Increase angle by pi/6 for each hit

    return baseAngle + angleAdjustment + randomAngleAdjustment;
}}
