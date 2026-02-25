package com.initializer.services;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.stereotype.Service;

import com.initializer.entity.NodeEntity;
import com.initializer.entity.EdgeEntity;
import com.initializer.graph.Edge;
import com.initializer.graph.Node;
import com.initializer.graph.NodeType;
import com.initializer.repository.NodeRepository;
import com.initializer.repository.EdgeRepository;

@Service
public class GraphService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public GraphService(NodeRepository nodeRepository,
                        EdgeRepository edgeRepository) {

        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;

    }

    private DirectedWeightedMultigraph<Node, Edge> buildGraphFromDatabase() {

        DirectedWeightedMultigraph<Node, Edge> g =
                new DirectedWeightedMultigraph<>(Edge.class);

        List<NodeEntity> nodeEntities = nodeRepository.findAll();
        List<EdgeEntity> edgeEntities = edgeRepository.findAll();

        // Map database nodes -> graph nodes
        java.util.Map<Integer, Node> nodeMap = new java.util.HashMap<>();

        for (NodeEntity entity : nodeEntities) {

            NodeType type = NodeType.valueOf(entity.getNodeType());

            Node node = new Node(
                entity.getNodeType(),
                type,
                entity.getDisplayName()
            );

            g.addVertex(node);
            nodeMap.put(entity.getNodeID(), node);
        }

        // Add edges
        for (EdgeEntity entity : edgeEntities) {

            Node source = nodeMap.get(entity.getSourceNode().getNodeID());
            Node target = nodeMap.get(entity.getTargetNode().getNodeID());

            Edge edge = new Edge(
                    entity.getAttackAction(),
                    entity.getBaseWeight()
            );

            g.addEdge(source, target, edge);
            g.setEdgeWeight(edge, entity.getBaseWeight());
        }

        System.out.println("Graph built dynamically from database.");

        return g;
    }

    public List<Node> getNodes() {
        DirectedWeightedMultigraph<Node, Edge> graph = buildGraphFromDatabase();
        return new ArrayList<>(graph.vertexSet());
    }

    public List<GraphEdgeDTO> getEdges() {

        DirectedWeightedMultigraph<Node, Edge> graph = buildGraphFromDatabase();
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

    public DirectedWeightedMultigraph<Node, Edge> getGraph() {
    return buildGraphFromDatabase();
}
}