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
import com.initializer.repository.MitigationRepository;
import com.initializer.entity.BusinessProfileEntity;

@Service
public class GraphService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final MitigationRepository mitigationRepository;

    public GraphService(NodeRepository nodeRepository,
                        EdgeRepository edgeRepository,
                        MitigationRepository mitigationRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.mitigationRepository = mitigationRepository;
}

    private DirectedWeightedMultigraph<Node, Edge> buildGraphFromDatabase(
        List<Integer> enabledMitigationIds) {

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

            int finalWeight = entity.getBaseWeight();

            if (enabledMitigationIds != null && !enabledMitigationIds.isEmpty()) {

                for (var effect : entity.getMitigationEffects()) {

                    if (enabledMitigationIds.contains(
                        effect.getMitigation().getMitID())) {

                    finalWeight += effect.getWeightModifier();
                }
            }
        }

        Edge edge = new Edge(
            entity.getAttackAction(),
            finalWeight
        );

        g.addEdge(source, target, edge);
        g.setEdgeWeight(edge, finalWeight);
        }

        System.out.println("Graph built dynamically from database.");

        return g;
    }

    public List<Node> getNodes() {
        DirectedWeightedMultigraph<Node, Edge> graph = buildGraphFromDatabase(null);
        return new ArrayList<>(graph.vertexSet());
    }

    public List<GraphEdgeDTO> getEdges() {

        DirectedWeightedMultigraph<Node, Edge> graph = buildGraphFromDatabase(null);
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

    public DirectedWeightedMultigraph<Node, Edge> getGraph(
        List<Integer> enabledMitigationIds) {

    return buildGraphFromDatabase(enabledMitigationIds);
}

public DirectedWeightedMultigraph<Node, Edge> getFilteredGraph(
        BusinessProfileEntity profile,
        List<Integer> enabledMitigationIds) {

    // Build full master graph first
    DirectedWeightedMultigraph<Node, Edge> graph =
            buildGraphFromDatabase(enabledMitigationIds);

    if (profile == null) {
        return graph; // no filtering if profile not provided
    }

    // Apply structural filtering based on profile toggles

    if (!profile.isUsesVPN()) {
        removeNodeById(graph, "VPN");
    }

    if (!profile.isHasFileServer()) {
        removeNodeById(graph, "FILE_SERVER");
    }

    if (!profile.isUsesSaaS()) {
        removeNodeById(graph, "THIRD_PARTY_SAAS");
    }

    if (!profile.isHasPublicWebApp()) {
        removeNodeById(graph, "WEB_APP");
    }

    if (!profile.isUsesIdentityProvider()) {
        removeNodeById(graph, "IDENTITY_PROVIDER");
    }

    if (!profile.isHasEmailServer()) {
        removeNodeById(graph, "EMAIL_SERVER");
    }

    if (!profile.isHasDomainController()) {
        removeNodeById(graph, "DOMAIN_CONTROLLER");
    }

    if (!profile.isHasInternalApp()) {
        removeNodeById(graph, "INTERNAL_APP");
    }

    if (!profile.isHasHRSystem()) {
        removeNodeById(graph, "HR_SYSTEM");
    }

    if (!profile.isHasFinanceSystem()) {
        removeNodeById(graph, "FINANCE_SYSTEM");
    }

    if (!profile.isHasBackupServer()) {
        removeNodeById(graph, "BACKUP_SERVER");
    }

    if (!profile.isHasMDMServer()) {
        removeNodeById(graph, "MDM_SERVER");
    }

    if (!profile.isHasWirelessAccessPoint()) {
        removeNodeById(graph, "WIRELESS_ACCESS_POINT");
    }

    if (!profile.isHasFirewall()) {
        removeNodeById(graph, "FIREWALL");
    }

    if (!profile.isHasDNSServer()) {
        removeNodeById(graph, "DNS_SERVER");
    }

    return graph;
}

private void removeNodeById(
        DirectedWeightedMultigraph<Node, Edge> graph,
        String nodeId) {

    Node nodeToRemove = graph.vertexSet()
            .stream()
            .filter(n -> n.getId().equals(nodeId))
            .findFirst()
            .orElse(null);

    if (nodeToRemove != null) {
        graph.removeVertex(nodeToRemove);
    }
}

public List<MitigationDTO> getMitigations(BusinessProfileEntity profile) {

    // Build the graph with the current profile filtering applied
    DirectedWeightedMultigraph<Node, Edge> graph =
            getFilteredGraph(profile, null);

    // Collect mitigation IDs tied to edges that still exist
    java.util.Set<Integer> mitigationIds = new java.util.HashSet<>();

    for (EdgeEntity edgeEntity : edgeRepository.findAll()) {

        String sourceId = edgeEntity.getSourceNode().getNodeType();
        String targetId = edgeEntity.getTargetNode().getNodeType();

        boolean edgeStillExists = graph.edgeSet().stream().anyMatch(edge ->
                graph.getEdgeSource(edge).getId().equals(sourceId) &&
                graph.getEdgeTarget(edge).getId().equals(targetId)
        );

        if (edgeStillExists) {

            edgeEntity.getMitigationEffects().forEach(effect ->
                    mitigationIds.add(effect.getMitigation().getMitID()));
        }
    }

    return mitigationRepository.findAllById(mitigationIds)
            .stream()
            .map(m -> new MitigationDTO(
                    m.getMitID(),
                    m.getMitName(),
                    m.getMitDesc()
            ))
            .toList();
}
}