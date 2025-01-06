package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
  // TODO
  private E state;
    private MCNode<E> parent;
    private ArrayList<MCNode<E>> children;
    private int visits;
    private double value;

    // Constructor
    public MCNode(E state, MCNode<E> parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.value = 0.0;
    }

    // Accessors and Mutators
    public E getState() {
        return state;
    }

    public MCNode<E> getParent() {
        return parent;
    }

    public ArrayList<MCNode<E>> getChildren() {
        return children;
    }

    public int getVisits() {
        return visits;
    }

    public double getValue() {
        return value;
    }

    public void addChild(MCNode<E> child) {
        children.add(child);
    }

    public void incrementVisits() {
        visits++;
    }

    public void addValue(double value) {
        this.value += value;
    }
}

