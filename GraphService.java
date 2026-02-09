package com.initializer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.initializer.graph.Edge;
import com.initializer.graph.Node;
import com.initializer.graph.NodeType;

@Service
public class GraphService {

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();
        // Sample nodes - replace with actual data loading logic
        nodes.add(new Node("web_server", NodeType.WEB_APP));
        nodes.add(new Node("database", NodeType.CUSTOMER_DB));
        nodes.add(new Node("employee_email", NodeType.EMPLOYEE_EMAIL));
        nodes.add(new Node("admin_account", NodeType.ADMIN_ACCOUNT));
        return nodes;
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        // Sample edges with source and target - replace with actual data loading logic
        edges.add(new Edge("web_server", "database", "SQL Injection", 5));
        edges.add(new Edge("employee_email", "web_server", "Phishing Attack", 3));
        edges.add(new Edge("database", "admin_account", "Privilege Escalation", 7));
        return edges;
    }

    public Node getNodeById(String id) {
        return getNodes().stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Edge getEdgeByAction(String action) {
        return getEdges().stream()
                .filter(edge -> edge.getAttackAction().equals(action))
                .findFirst()
                .orElse(null);
    }
}