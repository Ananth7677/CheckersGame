package edu.iastate.cs472.proj2;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * This panel lets two users play checkers against each other.
 * Red always starts the game.  If a player can jump an opponent's
 * piece, then the player must jump.  When a player can make no more
 * moves, the game ends.
 *
 * The class has a main() routine that lets it be run as a stand-alone
 * application.  The application just opens a window that uses an object
 * of type Checkers as its content pane.
 */
public class Checkers extends JPanel {

    static int aiKey = 0;
    static boolean chengeValue = false;

    public static void main(String[] args) {
        System.out.println("A Checker-Playing Agent Using the Monte Carlo Tree Search");
        System.out.println("keys: 1 (MCTS)  2 (Other - not implemented yet)\n");
        JFrame window = new JFrame("Checkers");
        Checkers content = new Checkers();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((screensize.width - window.getWidth()) / 2,
                (screensize.height - window.getHeight()) / 2);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
    }

    private JButton newGameButton;
    private JButton resignButton;
    private JLabel message;
    private static JLabel premessage;

    static PreBoard previous = new PreBoard();

    /**
     * The constructor creates the Board, adds all the components, and sets
     * the bounds of the components.
     */
    public Checkers() {
        setLayout(null);
        setPreferredSize(new Dimension(550, 250));
        setBackground(new Color(0, 150, 0));

        Board board = new Board();
        add(board);
        add(previous);
        add(newGameButton);
        add(resignButton);
        add(message);
        add(premessage);

        previous.setBounds(20, 20, 164, 164);
        board.setBounds(230, 20, 164, 164);
        newGameButton.setBounds(400, 60, 120, 30);
        resignButton.setBounds(400, 120, 120, 30);
        message.setBounds(140, 200, 350, 30);
        premessage.setBounds(40, 200, 350, 30);
    }

    /**
     * This panel displays a 160-by-160 checkerboard pattern with
     * a 2-pixel black border.  It is assumed that the size of the
     * panel is set to exactly 164-by-164 pixels.  This class does
     * the work of letting the users play checkers, and it displays
     * the checkerboard.
     */
    public static class PreBoard extends JPanel {
        CheckersData preBoard;
        CheckersMove moveAI;

        PreBoard() {
            premessage = new JLabel("", JLabel.LEFT);
            premessage.setFont(new Font("Serif", Font.BOLD, 14));
            premessage.setForeground(Color.green);
            premessage.setText("Initialization");
            preBoard = new CheckersData();
            preBoard.setUpGame();
            moveAI = new CheckersMove();
            repaint();
        }

        public void drawBoard(CheckersData currentBoard, CheckersMove move) {
            premessage = new JLabel("", JLabel.LEFT);
            premessage.setFont(new Font("Serif", Font.BOLD, 14));
            premessage.setForeground(Color.green);
            premessage.setText("Agent to Play");
            preBoard = copyBoard(currentBoard);
            moveAI = move.clone();
            repaint();
        }

        private CheckersData copyBoard(CheckersData board) {
            this.preBoard = board;
            CheckersData new_board = new CheckersData();
            for (int i = 0; i < board.board.length; i++) {
                for (int j = 0; j < 8; j++) {
                    new_board.board[i][j] = board.pieceAt(i, j);
                }
            }
            return new_board;
        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.black);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) g.setColor(Color.LIGHT_GRAY);
                    else g.setColor(Color.GRAY);
                    g.fillRect(2 + col * 20, 2 + row * 20, 20, 20);
                    switch (preBoard.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            break;
                        case CheckersData.BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            break;
                        case CheckersData.RED_KING:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                    }
                }
            }

            // highlight the AI's chosen move on the left board
            if (moveAI.rows.size() > 0) {
                g.setColor(Color.green);
                for (int i = 0; i < moveAI.rows.size(); i++) {
                    g.drawRect(2 + moveAI.cols.get(i) * 20, 2 + moveAI.rows.get(i) * 20, 19, 19);
                    g.drawRect(3 + moveAI.cols.get(i) * 20, 3 + moveAI.rows.get(i) * 20, 17, 17);
                }
            }
        }
    }

    private class Board extends JPanel implements ActionListener, MouseListener {
        CheckersData board;
        boolean gameInProgress;
        int currentPlayer;
        int selectedRow, selectedCol;
        CheckersMove[] legalMoves;
        AdversarialSearch player;
        CheckersData displayBoard;
        CheckersData agentBoard;

        Board() {
            setBackground(Color.BLACK);
            addMouseListener(this);
            resignButton = new JButton("Resign");
            resignButton.addActionListener(this);
            newGameButton = new JButton("New Game");
            newGameButton.addActionListener(this);
            message = new JLabel("", JLabel.CENTER);
            message.setFont(new Font("Serif", Font.BOLD, 14));
            message.setForeground(Color.green);
            board = new CheckersData();
            displayBoard = new CheckersData();
            agentBoard = new CheckersData();
            decideAIplayer();
            doNewGame();
        }

        public void decideAIplayer() {
            Scanner stdin = new Scanner(System.in);
            boolean done = false;
            player = new MonteCarloTreeSearch();
            while (!done) {
                try {
                    int aikey = stdin.nextInt();
                    if (aikey == 1) { done = true; aiKey = 1; }
                    else if (aikey == 2) { done = true; aiKey = 2; }
                    else System.out.println("\tThe entered number should be (1-2)");
                } catch (InputMismatchException e) {
                    System.out.println("\tInvalid input type (must be an integer)");
                    stdin.nextLine();
                }
            }
        }

        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == newGameButton) doNewGame();
            else if (src == resignButton) doResign();
        }

        void doNewGame() {
            if (gameInProgress) {
                message.setText("Finish the current game first!");
                return;
            }
            board.setUpGame();
            displayBoard.setUpGame();
            agentBoard.setUpGame();
            currentPlayer = CheckersData.RED;
            player.setCheckersData(board);
            legalMoves = board.getLegalMoves(CheckersData.RED);
            selectedRow = -1;
            message.setText("Red:  Make your move.");
            gameInProgress = true;
            newGameButton.setEnabled(false);
            resignButton.setEnabled(true);
            previous.drawBoard(agentBoard, new CheckersMove());
            repaint();
        }

        void doResign() {
            if (!gameInProgress) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == CheckersData.RED)
                gameOver("RED resigns.  BLACK wins.");
            else
                gameOver("BLACK resigns.  RED wins.");
        }

        void gameOver(String str) {
            message.setText(str);
            newGameButton.setEnabled(true);
            resignButton.setEnabled(false);
            gameInProgress = false;
            premessage.setText("Game is done");
        }

        /**
         * This is called by mousePressed() when a player clicks on the
         * square in the specified row and col.  It has already been checked
         * that a game is, in fact, in progress.
         */
        void doClickSquare(int row, int col) {
            for (CheckersMove legalMove : legalMoves) {
                if (legalMove.rows.get(0) == row && legalMove.cols.get(0) == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED)
                        message.setText("RED:  Make your move.");
                    else
                        message.setText("BLACK:  Make your move.");
                    repaint();
                    return;
                }
            }

            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }

            for (CheckersMove legalMove : legalMoves) {
                if (legalMove.rows.get(0) == selectedRow && legalMove.cols.get(0) == selectedCol
                        && legalMove.rows.get(legalMove.rows.size() - 1) == row
                        && legalMove.cols.get(legalMove.cols.size() - 1) == col) {
                    doMakeMove(legalMove);
                    return;
                }
            }

            message.setText("Click the square you want to move to.");
        }

        void doMakeMove(CheckersMove move) {
            board.makeMove(move);
            agentBoard = copyBoard(board);

            CheckersMove moveAI = new CheckersMove();

            if (currentPlayer == CheckersData.RED) {
                currentPlayer = CheckersData.BLACK;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null) {
                    gameOver("BLACK has no moves.  RED wins.");
                    displayBoard = copyBoard(board);
                    previous.drawBoard(board, moveAI);
                    repaint();
                    return;
                } else {
                    message.setText("BLACK:  Now AI's turn.");
                }

                player.setCheckersData(board);
                moveAI = player.makeMove(legalMoves);
                board.makeMove(moveAI);
                displayBoard = copyBoard(board);
                repaint();
            }

            previous.drawBoard(agentBoard, moveAI);

            currentPlayer = CheckersData.RED;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("RED has no moves.  BLACK wins.");
            else if (legalMoves[0].isJump())
                message.setText("RED:  Make your move.  You must jump.");
            else
                message.setText("RED:  Make your move.");

            selectedRow = -1;

            /* As a courtesy to the user, if all legal moves use the same piece, then
               select that piece automatically so the user won't have to click on it. */
            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                    if (legalMoves[i].rows.get(0) != legalMoves[0].rows.get(0)
                            || legalMoves[i].cols.get(0) != legalMoves[0].cols.get(0)) {
                        sameStartSquare = false;
                        break;
                    }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].rows.get(0);
                    selectedCol = legalMoves[0].cols.get(0);
                }
            }

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.black);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) g.setColor(Color.LIGHT_GRAY);
                    else g.setColor(Color.GRAY);
                    g.fillRect(2 + col * 20, 2 + row * 20, 20, 20);
                    switch (displayBoard.pieceAt(row, col)) {
                        case CheckersData.RED:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            break;
                        case CheckersData.BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            break;
                        case CheckersData.RED_KING:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col * 20, 4 + row * 20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col * 20, 16 + row * 20);
                            break;
                    }
                }
            }

            if (gameInProgress) {
                g.setColor(Color.cyan);
                for (CheckersMove legalMove : legalMoves) {
                    g.drawRect(2 + legalMove.cols.get(0) * 20, 2 + legalMove.rows.get(0) * 20, 19, 19);
                    g.drawRect(3 + legalMove.cols.get(0) * 20, 3 + legalMove.rows.get(0) * 20, 17, 17);
                }
                if (selectedRow >= 0) {
                    g.setColor(Color.white);
                    g.drawRect(2 + selectedCol * 20, 2 + selectedRow * 20, 19, 19);
                    g.drawRect(3 + selectedCol * 20, 3 + selectedRow * 20, 17, 17);
                    g.setColor(Color.green);
                    for (CheckersMove legalMove : legalMoves) {
                        if (legalMove.cols.get(0) == selectedCol && legalMove.rows.get(0) == selectedRow) {
                            for (int i = 1; i < legalMove.rows.size(); i++) {
                                g.drawRect(2 + legalMove.cols.get(i) * 20, 2 + legalMove.rows.get(i) * 20, 19, 19);
                                g.drawRect(3 + legalMove.cols.get(i) * 20, 3 + legalMove.rows.get(i) * 20, 17, 17);
                            }
                        }
                    }
                }
            }
        }

        private CheckersData copyBoard(CheckersData board) {
            this.board = board;
            CheckersData new_board = new CheckersData();
            for (int i = 0; i < board.board.length; i++) {
                for (int j = 0; j < 8; j++) {
                    new_board.board[i][j] = board.pieceAt(i, j);
                }
            }
            return new_board;
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (!gameInProgress)
                message.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (evt.getX() - 2) / 20;
                int row = (evt.getY() - 2) / 20;
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    doClickSquare(row, col);
            }
        }

        @Override public void mouseReleased(MouseEvent evt) { }
        @Override public void mouseClicked(MouseEvent evt) { }
        @Override public void mouseEntered(MouseEvent evt) { }
        @Override public void mouseExited(MouseEvent evt) { }
    }

    public void timeDelay(int t) {
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
