package com.initializer.graph;

// the Edge class represents an attacker action that allows movement from node to node
public class Edge {
    
    // source node id
    private String sourceId;

    // target node id
    private String targetId;

    // human readable description of the attacker action
    private String attackAction;

    // numeric weight that represents the difficulty/cost of performing this attack action (lower values = easier attacks)
    private int weight;
                                                  
    // constructor for creating an edge with source, target, attack action, and weight
    public Edge(String sourceId, String targetId, String attackAction, int weight) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.attackAction = attackAction;
        this.weight = weight;
    }

    // returns the source node id
    public String getSourceId() {
        return sourceId;
    }

    // returns the target node id
    public String getTargetId() {
        return targetId;
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
