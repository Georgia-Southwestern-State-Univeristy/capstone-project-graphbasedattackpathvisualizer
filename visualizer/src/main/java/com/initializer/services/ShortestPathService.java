package com.initializer.services;

import java.util.List;
import java.util.Optional;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.stereotype.Service;

import com.initializer.graph.Edge;
import com.initializer.graph.Node;

@Service
public class ShortestPathService {

    private final DirectedWeightedMultigraph<Node, Edge> graph;

    // Inject GraphService to obtain the existing graph instance
    // from GraphService and make sure a single shared graph is used
    public ShortestPathService(GraphService graphService) {
        this.graph = graphService.getGraph();
    }

    
    // Computes the shortest path between two node IDs using Dijkstra's algorithm 
    public List<Node> computeShortestPath(String sourceId, String targetId) {

        Node source = findNodeById(sourceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid source node ID: " + sourceId));

        Node target = findNodeById(targetId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid target node ID: " + targetId));

        DijkstraShortestPath<Node, Edge> dijkstra =
                new DijkstraShortestPath<>(graph);

        GraphPath<Node, Edge> path = dijkstra.getPath(source, target);

        if (path == null) {
            throw new IllegalStateException(
                    "No attack path exists between " + sourceId + " and " + targetId);
        }

        return path.getVertexList();
    }

    // Helper method to locate a node by ID inside the existing graph
    private Optional<Node> findNodeById(String id) {
        return graph.vertexSet()
                .stream()
                .filter(node -> node.getId().equalsIgnoreCase(id))
                .findFirst();
    }
}
