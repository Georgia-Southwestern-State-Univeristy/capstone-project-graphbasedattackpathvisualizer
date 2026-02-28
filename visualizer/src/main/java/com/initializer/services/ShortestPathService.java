package com.initializer.services;

import java.util.List;
import java.util.Optional;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.stereotype.Service;

import com.initializer.exception.InvalidNodeException;
import com.initializer.graph.Edge;
import com.initializer.graph.Node;
import com.initializer.graph.NodeType;

@Service
public class ShortestPathService {

    private final GraphService graphService;

    // Inject GraphService (do NOT store the graph itself)
    public ShortestPathService(GraphService graphService) {
        this.graphService = graphService;
    }

    // Computes the shortest attack path and returns ordered nodes, edges, and total path cost
    public AttackPathResult computeAttackPath(
        String sourceId,
        String targetId,
        List<Integer> enabledMitigationIds) {

        // Always get a fresh graph
        DirectedWeightedMultigraph<Node, Edge> graph =
        graphService.getGraph(enabledMitigationIds);

        Node source = findNodeById(graph, sourceId)
            .orElseThrow(() ->
                    new InvalidNodeException("Invalid source node ID: " + sourceId));

        Node target = findNodeById(graph, targetId)
            .orElseThrow(() ->
                    new InvalidNodeException("Invalid target node ID: " + targetId));

        DijkstraShortestPath<Node, Edge> dijkstra =
                new DijkstraShortestPath<>(graph);

        GraphPath<Node, Edge> path = dijkstra.getPath(source, target);

        if (path == null) {
            throw new InvalidNodeException(
                    "No attack path exists between " + sourceId + " and " + targetId);
        }

        return new AttackPathResult(
                path.getVertexList(),
                path.getEdgeList(),
                path.getWeight()
        );
    }

    // Returns attacker entry nodes from a fresh graph
    public List<Node> getAttackerEntryNodes() {

        DirectedWeightedMultigraph<Node, Edge> graph = graphService.getGraph(null);

        return graph.vertexSet()
            .stream()
            .filter(node -> node.getType() == NodeType.ATTACKER)
            .toList();
    }

    // Returns high-value target nodes from a fresh graph
    public List<Node> getHighValueTargetNodes() {

        DirectedWeightedMultigraph<Node, Edge> graph = graphService.getGraph(null);

        return graph.vertexSet()
            .stream()
            .filter(node -> node.getType() == NodeType.CUSTOMER_DB)
            .toList();
    }

    // Helper method to locate node by ID inside a provided graph
    private Optional<Node> findNodeById(
            DirectedWeightedMultigraph<Node, Edge> graph,
            String id) {

        return graph.vertexSet()
                .stream()
                .filter(node -> node.getId().equalsIgnoreCase(id))
                .findFirst();
    }
}
