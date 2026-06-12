package edu.iastate.cs472.proj2;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

    /**
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        MCNode<CheckersData> root = new MCNode<>(board, null);

        for (int i = 0; i < 10000; i++) {
            MCNode<CheckersData> selectedNode = selection(root);
            MCNode<CheckersData> expandedNode = expansion(selectedNode);
            double simulationResult = simulation(expandedNode);
            backPropagation(expandedNode, simulationResult);
        }

        MCNode<CheckersData> bestChild = root.getChildren().stream()
                .max(Comparator.comparingDouble(node -> node.getValue() / (node.getVisits() + 1e-6)))
                .orElse(null);

        if (bestChild == null) {
            return legalMoves[0];
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
        CheckersMove[] legalMoves = node.getState().getLegalMoves(CheckersData.BLACK);
        if (legalMoves == null || legalMoves.length == 0) {
            return node;
        }

        for (CheckersMove move : legalMoves) {
            CheckersData newState = node.getState().clone();
            newState.makeMove(move);
            MCNode<CheckersData> childNode = new MCNode<>(newState, node);
            node.addChild(childNode);
        }

        List<MCNode<CheckersData>> children = node.getChildren();
        return children.get(new Random().nextInt(children.size()));
    }

    private double simulation(MCNode<CheckersData> node) {
        CheckersData simulatedState = node.getState().clone();
        boolean isBlackTurn = true;

        while (!simulatedState.isGameOver()) {
            CheckersMove[] legalMoves = simulatedState.getLegalMoves(isBlackTurn ? CheckersData.BLACK : CheckersData.RED);
            if (legalMoves == null || legalMoves.length == 0) {
                break;
            }
            CheckersMove randomMove = legalMoves[new Random().nextInt(legalMoves.length)];
            simulatedState.makeMove(randomMove);
            isBlackTurn = !isBlackTurn;
        }

        return simulatedState.getWinner() == CheckersData.BLACK ? 1.0 : 0.0;
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

    private CheckersMove getMoveFromState(CheckersData state, CheckersMove[] legalMoves) {
        for (CheckersMove move : legalMoves) {
            CheckersData testState = board.clone();
            testState.makeMove(move);
            if (testState.equals(state)) {
                return move;
            }
        }
        return legalMoves[0];
    }
}
