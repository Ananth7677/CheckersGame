package edu.iastate.cs472.proj2;

/**
 * This class is to be extended by the class MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {
    protected CheckersData board;

    protected void setCheckersData(CheckersData board) {
        this.board = board;
    }

    protected CheckersMove[] legalMoves() {
        return board.getLegalMoves(CheckersData.BLACK);
    }

    /**
     * Return a move returned from the Monte Carlo tree search.
     *
     * @param legalMoves
     * @return CheckersMove
     */
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
