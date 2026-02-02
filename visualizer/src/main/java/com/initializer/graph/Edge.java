package com.initializer.graph;

// the Edge class represents an attacker action that allows movement from node to node
public class Edge {
    
    // human readable description of the attacker action
    private String attackAction;

    // numeric weight that represents the difficulty/cost of performing this attack action (lower values = easier attacks)
    private int weight;
                                                  
    // constructor for creating an edge with a specific attack action label and difficulty weight
    public Edge(String attackAction, int weight) {
        this.attackAction = attackAction;
        this.weight = weight;
    }

    // returns the attack action label for the edge
    public String getAttackAction() {
        return attackAction;
    }

    // returns the weight of the edge
    public int getWeight() {
        return weight;
    }

}
