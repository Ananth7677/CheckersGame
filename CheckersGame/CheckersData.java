package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData implements Cloneable {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4,
            VISITED = -3,
            STARTING_BLOCK = -4;



    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == col % 2) {
                    if (row < 3) {
                        board[row][col] = BLACK;
                    } else if (row > 4) {
                        board[row][col] = RED;
                    } else {
                        board[row][col] = EMPTY;
                    }
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (toRow == 0 && board[toRow][toCol] == RED) {
            board[toRow][toCol] = RED_KING;
        }
        if (toRow == 7 && board[toRow][toCol] == BLACK) {
            board[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        ArrayList<CheckersMove> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player) {
                    CheckersMove[] newMoves = getLegalJumpsFrom(player, row, col);
                    if (newMoves != null) {
                        for (CheckersMove move : newMoves) {
                            moves.add(move);
                        }
                    }
                }

                if (board[row][col] == player + 1) {
                    CheckersMove[] newMoves = getLegalJumpsFrom(player+1, row, col);
                    if (newMoves != null) {
                        for (CheckersMove move : newMoves) {
                            moves.add(move);
                        }
                    }
                }
            }
        }

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player) {
                        CheckersMove[] newMoves = getLegalMovesFrom(player, row, col);
                        if (newMoves != null) {
                            for (CheckersMove move : newMoves) {
                                moves.add(move);
                            }
                        }
                    }

                    if(board[row][col] == player+1){
                        CheckersMove[] newMoves = getLegalMovesFrom(player+1, row, col);
                        if (newMoves != null) {
                            for (CheckersMove move : newMoves) {
                                moves.add(move);
                            }
                        }
                    }
                }
            }
        }
        if (moves.size() == 0) {
            return null;
        } else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            moves.toArray(moveArray);
            return moveArray;
        }
    }

    CheckersMove[] getLegalMovesFrom(int player, int row, int col) {
        ArrayList<CheckersMove> moves = new ArrayList<>();

        // For regular RED player (can move forward only)
        if (player == RED) {
            if (canMove(player, row, col, row - 1, col + 1)) // Move upper-right
                moves.add(new CheckersMove(row, col, row - 1, col + 1));

            if (canMove(player, row, col, row - 1, col - 1)) // Move upper-left
                moves.add(new CheckersMove(row, col, row - 1, col - 1));
        }

        // For regular BLACK player (can move forward only)
        if (player == BLACK) {
            if (canMove(player, row, col, row + 1, col + 1)) // Move lower-right
                moves.add(new CheckersMove(row, col, row + 1, col + 1));

            if (canMove(player, row, col, row + 1, col - 1)) // Move lower-left
                moves.add(new CheckersMove(row, col, row + 1, col - 1));
        }

        // For RED_KING and BLACK_KING (can move in both directions)
        if (player == RED_KING || player == BLACK_KING) {
            if (canMove(player, row, col, row - 1, col + 1)) // Move upper-right
                moves.add(new CheckersMove(row, col, row - 1, col + 1));

            if (canMove(player, row, col, row - 1, col - 1)) // Move upper-left
                moves.add(new CheckersMove(row, col, row - 1, col - 1));

            if (canMove(player, row, col, row + 1, col + 1)) // Move lower-right
                moves.add(new CheckersMove(row, col, row + 1, col + 1));

            if (canMove(player, row, col, row + 1, col - 1)) // Move lower-left
                moves.add(new CheckersMove(row, col, row + 1, col - 1));
        }

        // If no valid moves, return null
        if (moves.size() == 0)
            return null;

        // Convert the ArrayList to an array and return
        CheckersMove[] moveArray = new CheckersMove[moves.size()];
        moves.toArray(moveArray);
        return moveArray;
    }



    private boolean canMove(int player, int r1, int c1, int r2, int c2) {
        // Check if the destination is out of bounds
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) return false;

        // Check if the destination square is empty
        if (board[r2][c2] != EMPTY) return false;

        // Handle RED and BLACK pieces
        if (player == RED) {
            return (board[r1][c1] == RED);
        } else if (player == BLACK) {
            return (board[r1][c1] == BLACK);
        }

        // Handle RED_KING and BLACK_KING (can move in any direction)
        if (player == RED_KING || player == BLACK_KING) {
            return (board[r1][c1] == RED_KING || board[r1][c1] == BLACK_KING);
        }

        return false;
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps.
     * Each move returned in the array represents a sequence of jumps
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        ArrayList<CheckersMove> moves = new ArrayList<>();

        // For RED player
        if (player == RED) {
            RedLegalJumps(player, row, col, moves);
        }

        // For BLACK player
        if (player == BLACK) {
            BlackLegalJumps(player, row, col, moves);
        }

        // For Kings (can move both up and down)
        if (player == RED_KING || player == BLACK_KING) {
            board[row][col] = STARTING_BLOCK;
            KingLegalJumps(player, row, col, moves);
            ResetBoard(player);
        }

        // If no moves are available, return null
        if (moves.size() == 0)
            return null;

        // Convert the list to an array and return it
        CheckersMove[] moveArray = new CheckersMove[moves.size()];
        moves.toArray(moveArray);
        return moveArray;
    }

    private void RecursiveJumps(int player, int row, int col, ArrayList<CheckersMove> currentSequence) {
        CheckersMove lastMove = currentSequence.isEmpty() ? new CheckersMove(row, col, row, col) : currentSequence.get(currentSequence.size() - 1).clone();

        boolean jumped = false;

        if (player == RED || player == RED_KING) {
            if (canJump(player, row, col, row - 2, col + 2, row - 1, col + 1)) {
                CheckersMove move = lastMove.clone();
                move.addMove(row - 2, col + 2);
                currentSequence.add(move);
                RecursiveJumps(player, row - 2, col + 2, currentSequence);
                jumped = true;
            }

            if (canJump(player, row, col, row - 2, col - 2, row - 1, col - 1)) {
                CheckersMove move = lastMove.clone();
                move.addMove(row - 2, col - 2);
                currentSequence.add(move);
                RecursiveJumps(player, row - 2, col - 2, currentSequence);
                jumped = true;
            }
        }

        if (player == BLACK || player == BLACK_KING) {
            if (canJump(player, row, col, row + 2, col + 2, row + 1, col + 1)) {
                CheckersMove move = lastMove.clone();
                move.addMove(row + 2, col + 2);
                currentSequence.add(move);
                RecursiveJumps(player, row + 2, col + 2, currentSequence);
                jumped = true;
            }

            if (canJump(player, row, col, row + 2, col - 2, row + 1, col - 1)) {
                CheckersMove move = lastMove.clone();
                move.addMove(row + 2, col - 2);
                currentSequence.add(move);
                RecursiveJumps(player, row + 2, col - 2, currentSequence);
                jumped = true;
            }
        }

        if (!jumped && !lastMove.equals(new CheckersMove(row, col, row, col))) {
            currentSequence.add(lastMove);
        }
    }

    private void ResetBoard(int player) {
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if(board[row][col] == VISITED) {
                    board[row][col] = EMPTY;
                }
                if(board[row][col] == STARTING_BLOCK) {
                    board[row][col] = player;
                }
            }
        }
    }

    private void IsSequence(ArrayList<CheckersMove> moves){
        for (int i = 0; i < moves.size(); i++) {
            for (int j = i; j < moves.size(); j++) {
                if(moves.get(i).rows == moves.get(j).rows){

                }
            }
        }
    }

    private void RedLegalJumps(int player, int row, int col, ArrayList<CheckersMove> moves) {
        if (canJump(player, row, col, row - 2, col + 2, row - 1, col + 1)){
            moves.add(new CheckersMove(row, col, row - 2, col + 2));
            RecursiveJumps(player, row-2, col+2, moves);
        }

        if (canJump(player, row, col, row - 2, col - 2, row - 1, col - 1)){
            moves.add(new CheckersMove(row, col, row - 2, col - 2));
            RecursiveJumps(player, row-2, col-2, moves);
        }
    }

    private void BlackLegalJumps(int player, int row, int col, ArrayList<CheckersMove> moves) {
        if (canJump(player, row, col, row + 2, col + 2, row + 1, col + 1)){
            moves.add(new CheckersMove(row, col, row + 2, col + 2));
            RecursiveJumps(player, row+2, col+2, moves);
        }

        if (canJump(player, row, col, row + 2, col - 2, row + 1, col - 1)){
            moves.add(new CheckersMove(row, col, row + 2, col - 2));
            RecursiveJumps(player, row+2, col-2, moves);
        }
    }

    private void KingLegalJumps(int player, int row, int col, ArrayList<CheckersMove> moves) {
        if (canJump(player, row, col, row - 2, col + 2, row - 1, col + 1)){
            if(!IsVisited(row-2, col+2)){
                moves.add(new CheckersMove(row, col, row - 2, col + 2));
                RecursiveJumps(player, row-2, col+2, moves);
            }
        }


        if (canJump(player, row, col, row - 2, col - 2, row - 1, col - 1)){
            if(!IsVisited(row-2, col-2)){
                moves.add(new CheckersMove(row, col, row - 2, col - 2));
                RecursiveJumps(player, row-2, col-2, moves);
            }
        }

        if (canJump(player, row, col, row + 2, col + 2, row + 1, col + 1)){
            if(!IsVisited(row+2, col+2)){
                moves.add(new CheckersMove(row, col, row + 2, col + 2));
                RecursiveJumps(player, row+2, col+2, moves);
            }
        }

        if (canJump(player, row, col, row + 2, col - 2, row + 1, col - 1)){
            if(!IsVisited(row+2, col-2)){
                moves.add(new CheckersMove(row, col, row + 2, col - 2));
                RecursiveJumps(player, row+2, col-2, moves);
            }
        }
    }

    private Boolean IsVisited(int row, int col) {
        return board[row][col] == VISITED;
    }

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) return false; // Intermediate square out of bounds
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) return false; // Destination square out of bounds

        // Destination square must be empty
        if (board[r2][c2] != EMPTY) return false;

        // Check if the intermediate square contains the opponent's piece
        if ((player == RED || player == RED_KING) && (board[r3][c3] != BLACK && board[r3][c3] != BLACK_KING)) {
            return false; // Intermediate square must contain an opponent's piece (for RED or RED_KING)
        }

        if ((player == BLACK || player == BLACK_KING) && (board[r3][c3] != RED && board[r3][c3] != RED_KING)) {
            return false; // Intermediate square must contain an opponent's piece (for BLACK or BLACK_KING)
        }

        return true;
    }

    private static CheckersMove[] mergeArrays(CheckersMove[] array1, CheckersMove[] array2) {
        CheckersMove[] mergedArray = new CheckersMove[array1.length + array2.length];

        // Copy elements of the first array
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);

        // Copy elements of the second array
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);

        return mergedArray;
    }

    @Override
    public CheckersData clone() {
        try {
            CheckersData copy = (CheckersData) super.clone();
            copy.board = new int[board.length][];
            for (int i = 0; i < board.length; i++) {
                copy.board[i] = board[i].clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }



    int getWinner() {

        if(getLegalMoves(RED) == null){
            return BLACK;
        } else if (getLegalMoves(BLACK) == null) {
            return RED;
        }
        return EMPTY;
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise.
     */
    boolean isGameOver() {
        // Example implementation to check if the game is over
        return getWinner() != EMPTY;
    }
}
