package superBomberman.mvc.model;

/**
 * Created by RAM0N on 11/28/15.
 */
public class GameBoard {

    public static final int ROW_COUNT = 13;
    public static final int COL_COUNT = 15;
    public static final int SQUARE_COUNT = 195;
    public static final int LEFT_OUTER_WALL_COL = 0;
    public static final int RIGHT_OUTER_WALL_COL = 14;
    public static final int BOTTOM_OUTER_WALL_ROW = 0;
    public static final int TOP_OUTER_WALL_ROW = 12;

    private Square[] mSquares;
    private int mLevel;

    public GameBoard(int level) {

        // assign level
        mLevel = level;

        // initialize square array
        mSquares = new Square[SQUARE_COUNT];

        createSquares();
        createExteriorWalls();
        createInteriorWalls();
        createMonsters();

    }

    private void createSquares() {
        int iSquareCount = 0;
        for (int iRow = 0; iRow < ROW_COUNT; iRow++) {
            for (int iCol = 0; iCol < COL_COUNT; iCol++) {
                mSquares[iSquareCount] = new Square(iRow, iCol);

                System.out.println("array spot: " + iSquareCount + ", " + mSquares[iSquareCount] + ", center: " + mSquares[iSquareCount].getCenter());
                iSquareCount++;
            }
        }
    }

    private void createExteriorWalls() {
        for (Square square : mSquares) {
            int iRow = square.getRow();
            int iCol = square.getColumn();
            if (iRow == BOTTOM_OUTER_WALL_ROW || iRow == TOP_OUTER_WALL_ROW
                    || iCol == LEFT_OUTER_WALL_COL || iCol == RIGHT_OUTER_WALL_COL) {
                Wall wall = new Wall(square, Wall.Type.SOLID);
                square.addWall();
                CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
            }
        }
    }

    private void createInteriorWalls() {
        if (mLevel == 1)
            createWallsForLevel1();

        // ADD CALLS & CODE FOR OTHER LEVELS

    }

    private void createMonsters() {
        if (mLevel == 1)
            createMonstersLevel1();

        // ADD CALLS & CODE FOR OTHER LEVELS
    }

    private void createWallsForLevel1() {

        for (Square square : mSquares) {
            int iRow = square.getRow();
            int iCol = square.getColumn();


            // inner solid walls
            // add shuffle to solid walls
            if ((iRow == 2 && (iCol == 2 || iCol == 4 || iCol == 5 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))
                    || (iRow == 3 && (iCol == 1 || iCol == 9))
                    || (iRow == 4 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))
                    || (iRow == 5 && (iCol == 3))
                    || (iRow == 6 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 9 || iCol == 10 || iCol == 12))
                    || (iRow == 7 && (iCol == 4 || iCol == 10))
                    || (iRow == 8 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 9 || iCol == 10 || iCol == 12))
                    || (iRow == 10 && (iCol == 2 || iCol == 4 || iCol == 6 || iCol == 8 || iCol == 10 || iCol == 12))) {

                // create wall, add to square, add to OpsList
                square.addWall();
                CommandCenter.getInstance().getOpsList().enqueue(new Wall(square, Wall.Type.SOLID), CollisionOp.Operation.ADD);
            }

            // inner breakable walls
            // add shuffle to rows
            if ((iRow == 1 && (iCol == 8 || iCol == 9))
                    || (iRow == 2 && (iCol == 3 || iCol == 7 || iCol == 9))
                    || (iRow == 3 && (iCol == 2 || iCol == 3 || iCol == 4 || iCol == 10))
                    || (iRow == 4 && (iCol == 1))
                    || (iRow == 5 && (iCol == 1 || iCol == 2 || iCol == 7 || iCol == 8 || iCol == 11 || iCol == 13))
                    || (iRow == 6 && (iCol == 7 || iCol == 11 || iCol == 13))
                    || (iRow == 7 && (iCol == 7 || iCol == 9))
                    || (iRow == 8 && (iCol == 5 || iCol == 7))
                    || (iRow == 9 && (iCol == 1 || iCol == 4 || iCol == 5 || iCol == 6 || iCol == 9 || iCol == 10 || iCol == 12))
                    || (iRow == 10 && (iCol == 3))) {

                // create wall, add to square, add to OpsList
                Wall wall = new Wall(square, Wall.Type.BREAKABLE);
                square.addWall();
                CommandCenter.getInstance().getOpsList().enqueue(wall, CollisionOp.Operation.ADD);
            }

            // add exit
            // shuffle among breakable walls
            if ((iRow == 1 && iCol == 8)) {
                square.addExit();
            }

        }

    }

    private void createMonstersLevel1() {
        for (Square square : mSquares) {
            int iRow = square.getRow();
            int iCol = square.getColumn();
            if ((iRow == 8 && (iCol == 13 || iCol == 1)) || (iRow == 6 && (iCol == 5))){
                CommandCenter.getInstance().getOpsList().enqueue(new Monster(square),CollisionOp.Operation.ADD);
            }
        }
    }


    public Square getSquare(int row, int col) {
        for (Square square : mSquares)
            if (square.getRow() == row && square.getColumn() == col)
                return square;
        throw new IllegalArgumentException("Square not found");
    }



}
