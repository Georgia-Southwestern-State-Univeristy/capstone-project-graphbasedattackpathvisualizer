package com.initializer.services;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.stereotype.Service;

import com.initializer.graph.Edge;
import com.initializer.graph.Node;
import com.initializer.graph.SmallBusinessAttackGraph;


// Service layer responsible for providing graph data
// extracted from the predefined JGraphT attack graph.

@Service
public class GraphService {

    private final DirectedWeightedMultigraph<Node, Edge> graph;

    public GraphService() {
        // Build the predefined small business attack graph
        this.graph = SmallBusinessAttackGraph.buildGraph();
    }

    
    // Returns all nodes in the attack graph.
    
    public List<Node> getNodes() {
        return new ArrayList<>(graph.vertexSet());
    }

    
    // Returns all edges in the attack graph with source and target information.
    
    public List<GraphEdgeDTO> getEdges() {
        List<GraphEdgeDTO> edges = new ArrayList<>();

        for (Edge edge : graph.edgeSet()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);

            edges.add(new GraphEdgeDTO(
                    source.getId(),
                    target.getId(),
                    edge.getAttackAction(),
                    edge.getWeight()
            ));
        }

        return edges;
    }
}