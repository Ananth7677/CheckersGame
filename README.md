# Checkers with Monte Carlo Tree Search

A Java Swing application where a human plays checkers against an AI agent powered by Monte Carlo Tree Search (MCTS).

## How It Works

The window shows two boards. The left board shows the state **before** the AI's move (with the AI's chosen move highlighted in green). The right board shows the **current game state** after each turn.

- **Red** (human) moves first and plays via mouse clicks.
- **Black** (AI) responds automatically using MCTS.

### The MCTS Agent

The agent runs 10,000 simulations per move using the standard four-phase loop:

1. **Selection** — traverse the tree using the UCB1 formula (exploration constant `C = √2`) to balance exploration vs. exploitation.
2. **Expansion** — generate child nodes for all legal Black moves from the selected node, then pick one at random.
3. **Simulation** — play out a random game to a terminal state; the playout returns a reward of `1.0` if Black wins and `0.0` otherwise.
4. **Backpropagation** — update visit counts and win values up the path to the root.

The best move is chosen as the child of the root with the highest average reward. Since tree nodes store board states rather than moves, the chosen move is recovered by applying each legal move to the current board and matching the resulting state.

## UI Guide

- **Cyan** outlines mark pieces that currently have a legal move.
- **White** outline marks the piece you've selected; **green** outlines mark the squares it can move to.
- If all legal moves start from the same piece, it is selected automatically.
- **New Game** starts a fresh game (only when no game is in progress); **Resign** forfeits the current game.
- Status messages appear below each board, including a "you must jump" prompt when a capture is forced.

## Project Structure

```
CheckersGame/
├── Checkers.java             # Swing UI (both boards), game loop, human input handling
├── CheckersData.java         # Board state, legal move generation, game rules, win detection
├── CheckersMove.java         # Move representation (single moves and multi-jump sequences)
├── AdversarialSearch.java    # Abstract base class for AI agents
├── MonteCarloTreeSearch.java # MCTS agent implementation
├── MCNode.java               # Generic tree node (state, parent, children, visits, value)
├── MCTree.java               # Tree wrapper (currently unused — MCTS works directly from the root MCNode)
└── Visited.java              # Row/col holder (currently unused — king jump tracking uses VISITED markers in CheckersData)
```

All classes are declared in the `edu.iastate.cs472.proj2` package, but the source files live flat in `CheckersGame/`.

## Requirements

- Java 8 or later
- No external dependencies

## Running the Game

From the `CheckersGame/` directory, compile into an output directory (required because of the package declaration):

```bash
javac -d out *.java
```

Run:

```bash
java -cp out edu.iastate.cs472.proj2.Checkers
```

The window opens, but the game does not start until you respond to the terminal prompt: enter `1` to use the MCTS agent. (Option `2` is a placeholder for a second agent that is not implemented yet.)

## Game Rules

- Pieces can only move diagonally on dark squares.
- **Red** moves toward lower row indices (up); **Black** moves toward higher row indices (down).
- If a jump is available, it must be taken. Multi-jump sequences are supported, and each generated jump move is a maximal sequence (jumps continue until no further jump is possible). Kings may not revisit a square within a single jump sequence.
- A piece reaching the opponent's back row becomes a **King** (marked `K`) and can move and jump in both directions.
- A player with no legal moves loses. A player may also resign at any time, conceding the game.
