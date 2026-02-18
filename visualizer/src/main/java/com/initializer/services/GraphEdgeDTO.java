package com.initializer.services;


// Data Transfer Object representing a graph edge
// with explicit source and target node identifiers.

public class GraphEdgeDTO {

    private String source;
    private String target;
    private String attackAction;
    private int weight;

    public GraphEdgeDTO(String source, String target, String attackAction, int weight) {
        this.source = source;
        this.target = target;
        this.attackAction = attackAction;
        this.weight = weight;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getAttackAction() {
        return attackAction;
    }

    public int getWeight() {
        return weight;
    }
}
