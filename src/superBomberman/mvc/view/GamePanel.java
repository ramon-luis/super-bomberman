package superBomberman.mvc.view;

import superBomberman.mvc.controller.Game;
import superBomberman.mvc.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends Panel {

    // ==============================================================
    // FIELDS
    // ==============================================================

    // The following "off" vars are used for the off-screen double-bufferred image.
    private Dimension dimOff;
    private Image imgOff;
    private Graphics grpOff;

    private GameFrame gmf;
    private Font fnt = new Font("SansSerif", Font.BOLD, 12);
    private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
    private Font fntTitle = new Font("Monospaced", Font.PLAIN, 12);
    private Font fntDescription = new Font("SansSerif", Font.BOLD + Font.ITALIC, 16);
    private FontMetrics fmt;
    private int nFontWidth;
    private int nFontHeight;
    private String strDisplay = "";

    private BufferedImage backgroundImage;


    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public GamePanel(Dimension dim) {
        gmf = new GameFrame();
        gmf.getContentPane().add(this);
        gmf.pack();
        initView();
        gmf.setSize(dim);
        gmf.setTitle("Super Bomberman");
        gmf.setResizable(false);
        gmf.setVisible(true);
        this.setFocusable(true);

    }


    // ==============================================================
    // METHODS
    // ==============================================================


    private void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("SCORE :  " + CommandCenter.getInstance().getScore(), GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, nFontHeight * 2);
    }

    public void drawBackground(Graphics g) {
        try {
            backgroundImage = ImageIO.read(new File("/Users/RAM0N/mpcs51036/proFinal/src/superBomberman/images/spaceBackground.png"));
            g.drawImage(backgroundImage, 0, 0, Game.DIM.width, Game.DIM.height, this);
        } catch (IOException e) {
            System.out.println("Error getting image: \n" + e);
        }
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("LEVEL :  " + CommandCenter.getInstance().getLevel(), GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, nFontHeight * 4);
    }


    @SuppressWarnings("unchecked")
    public void update(Graphics g) {
        if (grpOff == null || Game.DIM.width != dimOff.width
                || Game.DIM.height != dimOff.height) {
            dimOff = Game.DIM;
            imgOff = createImage(Game.DIM.width, Game.DIM.height);
            grpOff = imgOff.getGraphics();
        }

        // Fill in background with black
        //drawBackground(grpOff);
        grpOff.setColor(Color.BLACK);

        grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

        drawScore(grpOff);

        if (!CommandCenter.getInstance().isPlaying()) {
            displayTextOnScreen();
        } else if (CommandCenter.getInstance().isPaused()) {
            strDisplay = "Game Paused";
            grpOff.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
        }

        //playing and not paused!
        else {

            //draw them in decreasing level of importance
            //friends will be on top layer and debris on the bottom
            iterateMovables(grpOff,
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovFriends(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovFoes(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovPowerUps(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovBombs(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovWalls(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovBlasts(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovExits());


            drawNumberLivesLeft(grpOff);
            drawNumberBombs(grpOff);
            drawLevel(grpOff);
            if (CommandCenter.getInstance().isGameOver()) {
                CommandCenter.getInstance().setPlaying(false);
                //bPlaying = false;
            }
        }

        //draw the double-Buffered Image to the graphics context of the panel
        g.drawImage(imgOff, 0, 0, this);
    }


    //for each movable array, process it.
    private void iterateMovables(Graphics g, ArrayList<Movable>... movMovz) {

        for (ArrayList<Movable> movMovs : movMovz) {
            for (Movable mov : movMovs) {

                mov.move();
                mov.draw(g);

            }
        }

    }

    // Draw the number of bombs available
    private void drawNumberBombs(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("BOMBS:  " + CommandCenter.getInstance().getBomberman().getBombCount(),
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, 75);
    }


    // Draw the number of falcons left on the bottom-right of the screen.
    private void drawNumberLivesLeft(Graphics g) {
        Bomberman bomberman = CommandCenter.getInstance().getBomberman();
        double[] dLens = bomberman.getLengths();
        int nLen = bomberman.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];

        //convert to cartesean points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (15 * dLens[nC] * Math.sin(Math
                    .toRadians(180) + bomberman.getDegrees()[nC])),
                    (int) (15 * dLens[nC] * Math.cos(Math.toRadians(180)
                            + bomberman.getDegrees()[nC])));
        }

        //set the color to white
        g.setColor(Color.white);
        //for each falcon left (not including the one that is playing)
        for (int nD = 1; nD < CommandCenter.getInstance().getNumBombermans(); nD++) {
            //create x and y values for the objects to the bottom right using cartesean points again
            for (int nC = 0; nC < bomberman.getDegrees().length; nC++) {
                nXs[nC] = pntMs[nC].x + GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2 + (nD - 1) * nFontWidth + 6 + nD * 5;
                nYs[nC] = pntMs[nC].y + nFontHeight * 6 + 10;
            }
            g.fillPolygon(nXs, nYs, nLen);
        }
    }

    // REFACTOR TO DRAW BOMBERMAN TO CERTAIN SIZE
    private void drawTitleBomberMan(Graphics g, int xLocation, int yLocation) {
        Bomberman bomberman = new Bomberman(false);
        double[] dLens = bomberman.getLengths();
        int nLen = bomberman.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (75 * dLens[nC] * Math.sin(Math
                    .toRadians(180) + bomberman.getDegrees()[nC])),
                    (int) (75 * dLens[nC] * Math.cos(Math.toRadians(180)
                            + bomberman.getDegrees()[nC])));
        }

        //set the color to white
        g.setColor(Color.white);
        //for each falcon left (not including the one that is playing)
        //create x and y values for the objects to the bottom right using cartesean points again
        for (int nC = 0; nC < bomberman.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);
    }

    // REFACTOR TO DRAW BOMBERMAN TO CERTAIN SIZE
    private void drawTitleBomb() {

    }

    private void initView() {
        Graphics g = getGraphics();            // get the graphics context for the panel
        g.setFont(fnt);                        // take care of some simple font stuff
        fmt = g.getFontMetrics();
        nFontWidth = fmt.getMaxAdvance();
        nFontHeight = fmt.getHeight();
        g.setFont(fntBig);                    // set font info
    }


    // This method draws some text to the middle of the screen before/after a game
    private void displayTextOnScreen() {

        String title1 = " _______ __   __ _______ _______ ______     _______ _______ __   __ _______ _______ ______   __   __ _______ __    _ ";
        String title2 = "|       |  | |  |       |       |    _ |   |  _    |       |  |_|  |  _    |       |    _ | |  |_|  |   _   |  |  | |";
        String title3 = "|    ___|  | |  |    _  |    ___|   | ||   | |_|   |   _   |       | |_|   |    ___|   | || |       |  |_|  |   |_| |";
        String title4 = "| |_____|  |_|  |   |_| |   |___|   |_||_  |       |  | |  |       |       |   |___|   |_||_|       |       |       |";
        String title5 = "|_____  |       |    ___|    ___|    __  | |  _   ||  |_|  |       |  _   ||    ___|    __  |       |       |  _    |";
        String title6 = " _____| |       |   |   |   |___|   |  | | | |_|   |       | ||_|| | |_|   |   |___|   |  | | ||_|| |   _   | | |   |";
        String title7 = "|_______|_______|___|   |_______|___|  |_| |_______|_______|_|   |_|_______|_______|___|  |_|_|   |_|__| |__|_|  |__|";


        grpOff.setFont(fntTitle);
        grpOff.setColor(Color.YELLOW);
        int iTitleX = 45;  // hardcoded -> font metric did not calculate string width accurately
        grpOff.drawString(title1, iTitleX, nFontHeight * 3);
        grpOff.drawString(title2, iTitleX, nFontHeight * 4);
        grpOff.drawString(title3, iTitleX, nFontHeight * 5);
        grpOff.drawString(title4, iTitleX, nFontHeight * 6);
        grpOff.drawString(title5, iTitleX, nFontHeight * 7);
        grpOff.drawString(title6, iTitleX, nFontHeight * 8);
        grpOff.drawString(title7, iTitleX, nFontHeight * 9);

        drawTitleBomberMan(grpOff, iTitleX * 4, nFontHeight * 15);

        int iTextVerticalOffset = 30;

        grpOff.setFont(fntDescription);
        grpOff.setColor(Color.CYAN);
        strDisplay = "Defeat all monsters and then escape through the secret exit for each level";
        grpOff.drawString(strDisplay, iTitleX * 3, Game.DIM.height / 2 + nFontHeight + iTextVerticalOffset);

        grpOff.setFont(fnt);
        grpOff.setColor(Color.WHITE);

		strDisplay = "use the arrow keys to move";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
						+ nFontHeight + iTextVerticalOffset * 3);

		strDisplay = "use the space bar to set a bomb";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
						+ nFontHeight + iTextVerticalOffset * 4);

        grpOff.setFont(fnt);
		strDisplay = "'S' to Start";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
						+ nFontHeight + iTextVerticalOffset * 5);

		strDisplay = "'P' to Pause";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
						+ nFontHeight + iTextVerticalOffset * 6);

		strDisplay = "'Q' to Quit";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
						+ nFontHeight + iTextVerticalOffset * 7);

    }


    public GameFrame getFrm() {
        return this.gmf;
    }

    public void setFrm(GameFrame frm) {
        this.gmf = frm;
    }
}