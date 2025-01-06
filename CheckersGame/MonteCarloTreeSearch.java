//package edu.iastate.cs472.proj2;
//
///**
// *
// * @author
// *
// */
//
//import java.util.Comparator;
//import java.util.Random;
//
///**
// * This class implements the Monte Carlo tree search method to find the best
// * move at the current state.
// */
//public class MonteCarloTreeSearch extends AdversarialSearch {
//
//	/**
//     * The input parameter legalMoves contains all the possible moves.
//     * It contains four integers:  fromRow, fromCol, toRow, toCol
//     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
//     * It also provides a utility method `isJump` to see whether this
//     * move is a jump or a simple move.
//     *
//     * Each legalMove in the input now contains a single move
//     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
//     * (rows[2], cols[2]).
//     *
//     * @param legalMoves All the legal moves for the agent at current step.
//     */
//    public CheckersMove makeMove(CheckersMove[] legalMoves) {
//        // Initialize the root node with the current board state
//        MCNode<CheckersData> root = new MCNode<>(board, null);
//
//        // Perform Monte Carlo Tree Search to find the best move
//        for (int i = 0; i < 5000; i++) {
//            MCNode<CheckersData> selectedNode = selection(root);
//            MCNode<CheckersData> expandedNode = expansion(selectedNode);
//            double simulationResult = simulation(expandedNode);
//            backPropagation(expandedNode, simulationResult);
//        }
//
//        // Return the move corresponding to the best child of the root
//        MCNode<CheckersData> bestChild = root.getChildren().stream()
//                .max(Comparator.comparingDouble(MCNode::getValue))
//                .orElse(null);
//
//        if (bestChild == null) {
//            return legalMoves[0]; // Fallback to the first move
//        }
//
//        return getMoveFromState(bestChild.getState(), legalMoves);
//    }
//
//    // TODO
//    //
//    // Implement your helper methods here. They include at least the methods for selection,
//    // expansion, simulation, and back-propagation.
//    //
//    // For representation of the search tree, you are suggested (but limited) to use a
//    // child-sibling tree already implemented in the two classes CSTree and CSNode (which
//    // you may feel free to modify).  If you decide not to use the child-sibling tree, simply
//    // remove these two classes.
//    //
//    private MCNode<CheckersData> selection(MCNode<CheckersData> node) {
//        while (!node.getChildren().isEmpty()) {
//            node = node.getChildren().stream()
//                    .max(Comparator.comparingDouble(this::ucb1))
//                    .orElseThrow();
//        }
//        return node;
//    }
//
//    private MCNode<CheckersData> expansion(MCNode<CheckersData> node) {
//        CheckersMove[] legalMoves = node.getState().getLegalMoves(CheckersData.BLACK); // Assuming BLACK for this example
//        if (legalMoves == null || legalMoves.length == 0) {
//            return node; // No legal moves available, return the node itself
//        }
//        for (CheckersMove move : legalMoves) {
//            CheckersData newState = node.getState().clone();
//            newState.makeMove(move);
//            MCNode<CheckersData> childNode = new MCNode<>(newState, node);
//            node.addChild(childNode);
//        }
//        return node.getChildren().get(0); // Return the first expanded node for simplicity
//    }
//
//    private double simulation(MCNode<CheckersData> node) {
//        CheckersData simulatedState = node.getState().clone();
//        boolean isBlackTurn = true; // Start with Black's turn (computer)
//
//        // Simulate random play until the game ends
//        while (!simulatedState.isGameOver()) {
//            CheckersMove[] legalMoves = simulatedState.getLegalMoves(isBlackTurn ? CheckersData.BLACK : CheckersData.RED);
//            if (legalMoves == null || legalMoves.length == 0) {
//                break; // No legal moves available, end the simulation
//            }
//            CheckersMove randomMove = legalMoves[new Random().nextInt(legalMoves.length)];
//            simulatedState.makeMove(randomMove);
//            isBlackTurn = !isBlackTurn; // Alternate turns
//        }
//
//        return simulatedState.getWinner() == CheckersData.BLACK ? 1.0 : 0.0; // Assuming BLACK is the computer
//    }
//
//    private void backPropagation(MCNode<CheckersData> node, double result) {
//        while (node != null) {
//            node.incrementVisits();
//            node.addValue(result);
//            node = node.getParent();
//        }
//    }
//
//    private double ucb1(MCNode<CheckersData> node) {
//        double C = Math.sqrt(2);
//        double exploitation = node.getValue() / (node.getVisits() + 1e-6);
//        double exploration = C * Math.sqrt(Math.log(node.getParent().getVisits() + 1) / (node.getVisits() + 1e-6));
//        return exploitation + exploration;
//    }
//
//    private CheckersMove getMoveFromState(CheckersData state, CheckersMove[] legalMoves) {
//        for (CheckersMove move : legalMoves) {
//            CheckersData testState = board.clone();
//            testState.makeMove(move);
//            if (testState.equals(state)) {
//                return move;
//            }
//        }
//        return legalMoves[0]; // Fallback to the first move
//    }
//
//}


package edu.iastate.cs472.proj2;

/**
 *
 * @author
 *
 */

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // Initialize the root node with the current board state
        MCNode<CheckersData> root = new MCNode<>(board, null);

        // Perform Monte Carlo Tree Search to find the best move
        for (int i = 0; i < 10000; i++) { // Fixed iterations; can be made dynamic
            MCNode<CheckersData> selectedNode = selection(root);
            MCNode<CheckersData> expandedNode = expansion(selectedNode);
            double simulationResult = simulation(expandedNode);
            backPropagation(expandedNode, simulationResult);
        }

        // Return the move corresponding to the best child based on average reward
        MCNode<CheckersData> bestChild = root.getChildren().stream()
                .max(Comparator.comparingDouble(node -> node.getValue() / (node.getVisits() + 1e-6)))
                .orElse(null);

        if (bestChild == null) {
            return legalMoves[0]; // Fallback to the first move
        }

        return getMoveFromState(bestChild.getState(), legalMoves);
    }

    private MCNode<CheckersData> selection(MCNode<CheckersData> node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream()
                    .max(Comparator.comparingDouble(this::ucb1))
                    .orElseThrow();
        }
        return node;
    }

    private MCNode<CheckersData> expansion(MCNode<CheckersData> node) {
        CheckersMove[] legalMoves = node.getState().getLegalMoves(CheckersData.BLACK); // Assuming BLACK for this example
        if (legalMoves == null || legalMoves.length == 0) {
            return node; // No legal moves available, return the node itself
        }

        for (CheckersMove move : legalMoves) {
            CheckersData newState = node.getState().clone();
            newState.makeMove(move);
            MCNode<CheckersData> childNode = new MCNode<>(newState, node);
            node.addChild(childNode);
        }

        // Return a randomly selected expanded node for fairness
        List<MCNode<CheckersData>> children = node.getChildren();
        return children.get(new Random().nextInt(children.size()));
    }

    private double simulation(MCNode<CheckersData> node) {
        CheckersData simulatedState = node.getState().clone();
        boolean isBlackTurn = true; // Start with Black's turn (computer)

        // Simulate random play until the game ends
        while (!simulatedState.isGameOver()) {
            CheckersMove[] legalMoves = simulatedState.getLegalMoves(isBlackTurn ? CheckersData.BLACK : CheckersData.RED);
            if (legalMoves == null || legalMoves.length == 0) {
                break; // No legal moves available, end the simulation
            }
            CheckersMove randomMove = legalMoves[new Random().nextInt(legalMoves.length)];
            simulatedState.makeMove(randomMove);
            isBlackTurn = !isBlackTurn; // Alternate turns
        }

        return simulatedState.getWinner() == CheckersData.BLACK ? 1.0 : 0.0; // Assuming BLACK is the computer
    }

    private void backPropagation(MCNode<CheckersData> node, double result) {
        while (node != null) {
            node.incrementVisits();
            node.addValue(result);
            node = node.getParent();
        }
    }

    private double ucb1(MCNode<CheckersData> node) {
        double C = Math.sqrt(2);
        double exploitation = node.getValue() / (node.getVisits() + 1e-6);
        double exploration = C * Math.sqrt(Math.log(node.getParent().getVisits() + 1) / (node.getVisits() + 1e-6));
        return exploitation + exploration;
    }

    private double ucb2(MCNode<CheckersData> node) {
        double C = 0.5;
        double exploitation = node.getValue() / (node.getVisits() + 1e-6);
        double exploration = C * Math.sqrt(Math.log(node.getParent().getVisits() + 1) / (node.getVisits() + 1e-6));
        return exploitation + exploration;
    }

    private CheckersMove getMoveFromState(CheckersData state, CheckersMove[] legalMoves) {
        for (CheckersMove move : legalMoves) {
            CheckersData testState = board.clone();
            testState.makeMove(move);
            if (testState.equals(state)) {
                return move;
            }
        }
        return legalMoves[0]; // Fallback to the first move
    }
}

