package com.initializer.graph;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DirectedWeightedMultigraph;


 // Builds the predefined small business attack graph for the MVP.
public class SmallBusinessAttackGraph {

     //Creates and returns the predefined small business attack graph.
    public static DirectedWeightedMultigraph<Node, Edge> buildGraph() {

        // Graph instance
        DirectedWeightedMultigraph<Node, Edge> graph =
                new DirectedWeightedMultigraph<>(Edge.class);

            // Predefined small business nodes (one per NodeType)
            Node attacker = new Node("attacker", NodeType.ATTACKER);
            Node employeeEmail = new Node("employeeEmail", NodeType.EMPLOYEE_EMAIL);
            Node employeeWorkstation = new Node("employeeWorkstation", NodeType.EMPLOYEE_WORKSTATION);
            Node identityProvider = new Node("identityProvider", NodeType.IDENTITY_PROVIDER);
            Node adminAccount = new Node("adminAccount", NodeType.ADMIN_ACCOUNT);
            Node customerDb = new Node("customerDb", NodeType.CUSTOMER_DB);
            Node webApp = new Node("webApp", NodeType.WEB_APP);
            Node vpn = new Node("vpn", NodeType.VPN);
            Node thirdPartySaas = new Node("thirdPartySaas", NodeType.THIRD_PARTY_SAAS);
            Node fileServer = new Node("fileServer", NodeType.FILE_SERVER);

            // Add nodes to the graph
            graph.addVertex(attacker);
            graph.addVertex(employeeEmail);
            graph.addVertex(employeeWorkstation);
            graph.addVertex(identityProvider);
            graph.addVertex(adminAccount);
            graph.addVertex(customerDb);
            graph.addVertex(webApp);
            graph.addVertex(vpn);
            graph.addVertex(thirdPartySaas);
            graph.addVertex(fileServer);

            // Maps node types to their corresponding node instances for easy edge creation
            Map<NodeType, Node> nodeMap = new HashMap<>();
            nodeMap.put(NodeType.ATTACKER, attacker);
            nodeMap.put(NodeType.EMPLOYEE_EMAIL, employeeEmail);
            nodeMap.put(NodeType.EMPLOYEE_WORKSTATION, employeeWorkstation);
            nodeMap.put(NodeType.IDENTITY_PROVIDER, identityProvider);
            nodeMap.put(NodeType.ADMIN_ACCOUNT, adminAccount);
            nodeMap.put(NodeType.CUSTOMER_DB, customerDb);
            nodeMap.put(NodeType.WEB_APP, webApp);
            nodeMap.put(NodeType.VPN, vpn);
            nodeMap.put(NodeType.THIRD_PARTY_SAAS, thirdPartySaas);
            nodeMap.put(NodeType.FILE_SERVER, fileServer);

            // Add attack edges based on predefined attack edge catalog
            for (AttackEdgeDefinition def : AttackEdgeCatalog.ATTACK_EDGES) {

                Node fromNode = nodeMap.get(def.getFrom());
                Node toNode = nodeMap.get(def.getTo());

                Edge edge = new Edge(def.getLabel(), def.getWeight());

                graph.addEdge(fromNode, toNode, edge);
                graph.setEdgeWeight(edge, def.getWeight());
            }

        return graph;
    }
}
