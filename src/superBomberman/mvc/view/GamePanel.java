package superBomberman.mvc.view;

import superBomberman.mvc.controller.Game;
import superBomberman.mvc.model.*;
import superBomberman.mvc.model.Sprite.Direction;

import java.awt.*;
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

    // game frame methods
    public GameFrame getFrm() {
        return this.gmf;
    }

    public void setFrm(GameFrame frm) {
        this.gmf = frm;
    }

    // initialize the view
    private void initView() {
        Graphics g = getGraphics();            // get the graphics context for the panel
        g.setFont(fnt);                        // take care of some simple font stuff
        fmt = g.getFontMetrics();
        nFontWidth = fmt.getMaxAdvance();
        nFontHeight = fmt.getHeight();
        g.setFont(fntBig);                    // set font info
    }

    @SuppressWarnings("unchecked")
    public void update(Graphics g) {
        // create graphics if null
        if (grpOff == null || Game.DIM.width != dimOff.width
                || Game.DIM.height != dimOff.height) {
            dimOff = Game.DIM;
            imgOff = createImage(Game.DIM.width, Game.DIM.height);
            grpOff = imgOff.getGraphics();
        }

        // Fill in background with black
        grpOff.setColor(Color.BLACK);
        grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

        // if game is not being played and all levels are not complete, then show welcome screen
        // if game is paused, then show paused screen
        // if all levels are complete, then show victory screen
        if (!CommandCenter.getInstance().isPlaying() && !CommandCenter.getInstance().allLevelsComplete()) {
            drawGameWelcomeScreen();
        } else if (CommandCenter.getInstance().isPaused()) {
            drawPausedScreen();
        } else if (CommandCenter.getInstance().allLevelsComplete()) {
            drawVictoryScreen();
        }

        // draw the game:
        else {
            // draw the sidebar, move and draw all movable objects
            drawSideBarInfo();
            iterateMovables(grpOff,
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovWalls(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovExits(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovPowerUps(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovFriends(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovEnemies(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovBlasts(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovBombs(),
                    (ArrayList<Movable>) CommandCenter.getInstance().getMovDisplays());
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

    private void drawSideBarInfo() {
        drawScore(grpOff);
        drawLevel(grpOff);
        drawNumberBombs(grpOff);
        drawBlastPower(grpOff);
        drawKickAbility(grpOff);
        drawNumberLivesLeft(grpOff);
    }

    // draw the score
    private void drawScore(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(fnt);
        g.drawString("SCORE :  " + String.format("%,d", CommandCenter.getInstance().getScore()),
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 2);
    }

    // draw the level
    private void drawLevel(Graphics g) {
        g.setColor(Color.CYAN);
        g.setFont(fnt);
        g.drawString("LEVEL :  " + CommandCenter.getInstance().getLevel(),
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 3);
    }

    // draw the number of bombs available
    private void drawNumberBombs(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("BOMBS:  " + CommandCenter.getInstance().getBomberman().getBombCount(),
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 5);
    }

    // Draw the number of bombs available
    private void drawBlastPower(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("BLAST POWER:  " + CommandCenter.getInstance().getBomberman().getBlastPower(),
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 6);
    }

    // draw the kick ability -> only occurs if bomberman has kick ability
    private void drawKickAbility(Graphics g) {
        if (CommandCenter.getInstance().getBomberman().hasKickAbility()) {
            g.setColor(Color.RED);
            g.setFont(fnt);
            g.drawString("KICK POWER ACTIVE",
                    GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 8);
        }
    }

    // draw the number of lives left
    private void drawNumberLivesLeft(Graphics g) {

        // draw the label
        g.setColor(Color.white);
        g.setFont(fnt);
        g.drawString("LIVES REMAINING",
                GameBoard.COL_COUNT * Square.SQUARE_LENGTH + Square.SQUARE_LENGTH / 2, (nFontHeight + 2) * 10);

        // draw an individual bomberman for each life remaining
        for (int nD = 1; nD < CommandCenter.getInstance().getNumBombermans(); nD++) {
            int xLocation = GameBoard.COL_COUNT * Square.SQUARE_LENGTH
                    + Square.SQUARE_LENGTH / 2 + (nD - 1) * nFontWidth + 6 + nD * 5;
            int yLocation = (nFontHeight + 2) * 11 + 5;
            drawBomberman(g, xLocation, yLocation, 15);
        }
    }

    private void drawPausedScreen() {
        strDisplay = "Game Paused";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
    }

    private void drawVictoryScreen() {

        // draw the objects
        drawBomberman(grpOff, Game.DIM.width / 2 - 150, Game.DIM.height / 2 - 100, 250);
        drawBomb(grpOff, Game.DIM.width / 2 + 50, Game.DIM.height / 2 + 50, 75);
        drawBlast(grpOff, Game.DIM.width - 200, 150, 150, null);
        drawDrone(grpOff, Game.DIM.width - 125, 100, 65);
        drawAlien(grpOff, Game.DIM.width - 200, 150, 75);
        drawSoldier(grpOff, Game.DIM.width - 275, 200, 75);

        // set the font
        grpOff.setFont(fntBig);

        // display text
        grpOff.setColor(Color.YELLOW);
        strDisplay = "All enemies defeated!  All levels complete!";
        grpOff.drawString(strDisplay, 50, Game.DIM.height / 2 + 175);

        // display text
        grpOff.setColor(Color.WHITE);
        strDisplay = "Final Score:  " + String.format("%,d", CommandCenter.getInstance().getScore());
        grpOff.drawString(strDisplay, 300, Game.DIM.height / 2 + 215);

        // display text
        grpOff.setColor(Color.CYAN);
        strDisplay = "'S' to Start Game Over";
        grpOff.drawString(strDisplay, 250, Game.DIM.height / 2 + 255);
    }

    // draw the welcome screen
    private void drawGameWelcomeScreen() {

        // variables to store the ASCII art for the title
        String title1 = " _______ __   __ _______ _______ ______     _______ _______ __   __ _______ _______ ______   __   __ _______ __    _ ";
        String title2 = "|       |  | |  |       |       |    _ |   |  _    |       |  |_|  |  _    |       |    _ | |  |_|  |   _   |  |  | |";
        String title3 = "|    ___|  | |  |    _  |    ___|   | ||   | |_|   |   _   |       | |_|   |    ___|   | || |       |  |_|  |   |_| |";
        String title4 = "| |_____|  |_|  |   |_| |   |___|   |_||_  |       |  | |  |       |       |   |___|   |_||_|       |       |       |";
        String title5 = "|_____  |       |    ___|    ___|    __  | |  _   ||  |_|  |       |  _   ||    ___|    __  |       |       |  _    |";
        String title6 = " _____| |       |   |   |   |___|   |  | | | |_|   |       | ||_|| | |_|   |   |___|   |  | | ||_|| |   _   | | |   |";
        String title7 = "|_______|_______|___|   |_______|___|  |_| |_______|_______|_|   |_|_______|_______|___|  |_|_|   |_|__| |__|_|  |__|";


        // draw the ASCII art title
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

        // draw some blasts
        drawBlast(grpOff, iTitleX * 6, nFontHeight * 19, 50, null);
        drawBlast(grpOff, iTitleX * 7, nFontHeight * 19, 40, Direction.RIGHT);
        drawBlast(grpOff, iTitleX * 8, nFontHeight * 19, 30, Direction.RIGHT);
        drawBlast(grpOff, iTitleX * 8 + iTitleX / 2, nFontHeight * 19, 25, Direction.RIGHT);
        drawBlast(grpOff, iTitleX * 9, nFontHeight * 19, 20, Direction.RIGHT);

        // draw a bomberman & bomb
        drawBomberman(grpOff, iTitleX * 4, nFontHeight * 17, 75);
        drawBomb(grpOff, iTitleX * 6, nFontHeight * 19, 40);

        // draw some enemies
        drawAlien(grpOff, iTitleX * 12, nFontHeight * 17, 75);
        drawDrone(grpOff, iTitleX * 14 + iTitleX / 2, nFontHeight * 17, 50);
        drawSoldier(grpOff, iTitleX * 17, nFontHeight * 17, 75);

        // offset used for text display below
        int iTextVerticalOffset = 30;

        // draw text
        grpOff.setFont(fntDescription);
        grpOff.setColor(Color.CYAN);
        strDisplay = "Defeat all monsters and then escape through the secret exit for each level";
        grpOff.drawString(strDisplay, iTitleX * 3, Game.DIM.height / 2 + nFontHeight + iTextVerticalOffset);

        // draw text
        grpOff.setFont(fnt);
        grpOff.setColor(Color.WHITE);

        strDisplay = "ARROW KEYS to move";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 3);

        strDisplay = "SPACE BAR to set a bomb";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 4);

        strDisplay = "'K' to kick a bomb (POWER UP)";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 5);

        grpOff.setFont(fnt);
        strDisplay = "'S' to Start";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 6);

        strDisplay = "'P' to Pause";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 7);

        strDisplay = "'Q' to Quit";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 2
                        + nFontHeight + iTextVerticalOffset * 8);

    }


    // draw a bomberman
    private void drawBomberman(Graphics g, int xLocation, int yLocation, int size) {
        Bomberman bomberman = new Bomberman();
        double[] dLens = bomberman.getLengths();
        int nLen = bomberman.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(180) + bomberman.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(180)
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

    // draw a bomb
    private void drawBomb(Graphics g, int xLocation, int yLocation, int size) {
        Bomb bomb = new Bomb();
        double[] dLens = bomb.getLengths();
        int nLen = bomb.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(180) + bomb.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(180)
                            + bomb.getDegrees()[nC])));
        }

        //set the color to white
        g.setColor(Color.white);
        //create x and y values for the object to the bottom right using cartesian points again
        for (int nC = 0; nC < bomb.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);

        // set coordinates to draw the circle of bomb
        int iDrawX = xLocation - iSize + iSize / 4;
        int iDrawY = yLocation - iSize + iSize / 4;

        // set color & draw circle for bomb
        g.setColor(Color.DARK_GRAY);
        g.fillOval(iDrawX, iDrawY, iSize * 2 - 15, iSize * 2 - 15);

    }

    // draw a blast
    private void drawBlast(Graphics g, int xLocation, int yLocation, int size, Direction direction) {
        Blast blast = new Blast(direction);
        double[] dLens = blast.getLengths();
        int nLen = blast.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(180) + blast.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(180)
                            + blast.getDegrees()[nC])));
        }

        //set the color, create x & y values, fill shape
        g.setColor(Color.orange);
        for (int nC = 0; nC < blast.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);
    }

    // draw a soldier
    private void drawSoldier(Graphics g, int xLocation, int yLocation, int size) {
        Soldier soldier = new Soldier();
        double[] dLens = soldier.getLengths();
        int nLen = soldier.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(180) + soldier.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(180)
                            + soldier.getDegrees()[nC])));
        }

        //set the color, create x & y values, fill shape
        g.setColor(Color.red);
        for (int nC = 0; nC < soldier.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);
    }

    // draw an alien
    private void drawAlien(Graphics g, int xLocation, int yLocation, int size) {
        Alien alien = new Alien();
        double[] dLens = alien.getLengths();
        int nLen = alien.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(180) + alien.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(180)
                            + alien.getDegrees()[nC])));
        }

        //set the color, create x & y values, fill shape
        g.setColor(Color.green);
        for (int nC = 0; nC < alien.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);

        // draw outer eye
        int iDrawX = xLocation - size / 4;
        int iDrawY = yLocation - size / 2;
        g.setColor(Color.WHITE);
        g.fillOval(iDrawX, iDrawY, size / 2, size / 2);

        // draw inner eye
        iDrawX = xLocation - size / 8;
        iDrawY = yLocation - size / 3;
        g.setColor(Color.BLACK);
        g.fillOval(iDrawX, iDrawY, size / 4, size / 4);


    }

    // draw a drone
    private void drawDrone(Graphics g, int xLocation, int yLocation, int size) {
        Drone drone = new Drone();
        double[] dLens = drone.getLengths();
        int nLen = drone.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];
        int iSize = size;

        //convert to cartesian points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (iSize * dLens[nC] * Math.sin(Math
                    .toRadians(45) + drone.getDegrees()[nC])),
                    (int) (iSize * dLens[nC] * Math.cos(Math.toRadians(45)
                            + drone.getDegrees()[nC])));
        }

        //set the color, create x & y values, fill shape
        g.setColor(Color.lightGray);
        for (int nC = 0; nC < drone.getDegrees().length; nC++) {
            nXs[nC] = pntMs[nC].x + xLocation;
            nYs[nC] = pntMs[nC].y + yLocation;
        }
        g.fillPolygon(nXs, nYs, nLen);
    }
}