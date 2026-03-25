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
import com.initializer.entity.BusinessProfileEntity;
import com.initializer.entity.UserEntity;

@Service
public class ShortestPathService {

    private final GraphService graphService;
    private final BusinessProfileService businessProfileService;

    // Inject GraphService (do NOT store the graph itself)
    public ShortestPathService(GraphService graphService,
                               BusinessProfileService businessProfileService) {
        this.graphService = graphService;
        this.businessProfileService = businessProfileService;
    }

    // Computes the shortest attack path for the authenticated user's profile
    public AttackPathResult computeAttackPath(
        UserEntity user,
        String sourceId,
        String targetId,
        List<Integer> enabledMitigationIds) {

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            throw new InvalidNodeException("Business profile not configured.");
        }

        DirectedWeightedMultigraph<Node, Edge> graph =
                graphService.getFilteredGraph(profile, enabledMitigationIds);

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

    // Returns attacker entry nodes from the authenticated user's graph
    public List<Node> getAttackerEntryNodes(UserEntity user) {

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            throw new InvalidNodeException("Business profile not configured.");
        }

        DirectedWeightedMultigraph<Node, Edge> graph =
                graphService.getFilteredGraph(profile, null);

        return graph.vertexSet()
            .stream()
            .filter(node -> node.getType() == NodeType.ATTACKER)
            .toList();
    }

    // Returns high-value target nodes from the authenticated user's graph
    public List<Node> getHighValueTargetNodes(UserEntity user) {

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            throw new InvalidNodeException("Business profile not configured.");
        }

        DirectedWeightedMultigraph<Node, Edge> graph =
                graphService.getFilteredGraph(profile, null);

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