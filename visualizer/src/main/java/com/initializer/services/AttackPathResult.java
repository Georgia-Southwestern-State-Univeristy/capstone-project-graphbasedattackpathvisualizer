package com.initializer.services;

import java.util.List;
import com.initializer.graph.Edge;
import com.initializer.graph.Node;


// DTO representing a computed attack path.
// Contains ordered nodes, ordered edges, and total path cost.

public class AttackPathResult {

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final double totalCost;

    public AttackPathResult(List<Node> nodes, List<Edge> edges, double totalCost) {
        this.nodes = nodes;
        this.edges = edges;
        this.totalCost = totalCost;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public double getTotalCost() {
        return totalCost;
    }
}
