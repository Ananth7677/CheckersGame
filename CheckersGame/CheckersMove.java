package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * A CheckersMove object represents a move in the game of Checkers.
 * It holds a sequence of rows and columns visited by a piece, supporting
 * both single moves and multi-jump sequences.
 *
 * Example:
 *   single move:          (2, 0) -> (3, 1)
 *   sequence of jumps:    (2, 0) -> (4, 2) -> (6, 0)
 */
public class CheckersMove {

    ArrayList<Integer> rows = new ArrayList<Integer>();
    ArrayList<Integer> cols = new ArrayList<Integer>();

    CheckersMove(int r1, int c1, int r2, int c2) {
        rows.add(r1);
        cols.add(c1);
        rows.add(r2);
        cols.add(c2);
    }

    CheckersMove() {
    }

    boolean isJump() {
        return (rows.get(0) - rows.get(1) == 2 || rows.get(0) - rows.get(1) == -2);
    }

    void addMove(int r, int c) {
        rows.add(r);
        cols.add(c);
    }

    @Override
    public CheckersMove clone() {
        CheckersMove move = new CheckersMove();
        move.rows.addAll(this.rows);
        move.cols.addAll(this.cols);
        return move;
    }

}  // end class CheckersMove.
