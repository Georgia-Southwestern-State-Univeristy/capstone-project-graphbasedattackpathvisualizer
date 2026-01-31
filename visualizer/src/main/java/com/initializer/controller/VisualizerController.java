package com.initializer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VisualizerController {

    @GetMapping("/health")
    public String health() {
        return "Visualizer API is running";
    }

    // Graph retrieval endpoints
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph() {
        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", getMockNodes());
        graph.put("edges", getMockEdges());
        return ResponseEntity.ok(graph);
    }

    @GetMapping("/graph/nodes")
    public ResponseEntity<List<Map<String, Object>>> getNodes() {
        return ResponseEntity.ok(getMockNodes());
    }

    @GetMapping("/graph/edges")
    public ResponseEntity<List<Map<String, Object>>> getEdges() {
        return ResponseEntity.ok(getMockEdges());
    }

    // Path computation endpoints
    @PostMapping("/paths/compute")
    public ResponseEntity<Map<String, Object>> computePath(@RequestBody Map<String, Object> request) {
        // Placeholder: expects { "source": "nodeId", "target": "nodeId" }
        String source = (String) request.get("source");
        String target = (String) request.get("target");

        Map<String, Object> result = new HashMap<>();
        result.put("pathId", UUID.randomUUID().toString());
        result.put("source", source);
        result.put("target", target);
        result.put("path", Arrays.asList(source, "intermediate_node", target));
        result.put("risk", "HIGH");
        result.put("steps", 3);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/paths/{pathId}")
    public ResponseEntity<Map<String, Object>> getPath(@PathVariable String pathId) {
        Map<String, Object> path = new HashMap<>();
        path.put("pathId", pathId);
        path.put("source", "start_node");
        path.put("target", "end_node");
        path.put("path", Arrays.asList("start_node", "intermediate_node", "end_node"));
        path.put("risk", "HIGH");
        path.put("description", "Sample attack path");

        return ResponseEntity.ok(path);
    }

    // Mock data methods
    private List<Map<String, Object>> getMockNodes() {
        List<Map<String, Object>> nodes = new ArrayList<>();
        Map<String, Object> node1 = new HashMap<>();
        node1.put("id", "web_server");
        node1.put("label", "Web Server");
        node1.put("type", "asset");
        node1.put("vulnerabilities", Arrays.asList("CVE-2023-1234", "CVE-2023-5678"));

        Map<String, Object> node2 = new HashMap<>();
        node2.put("id", "database");
        node2.put("label", "Database");
        node2.put("type", "asset");
        node2.put("vulnerabilities", Arrays.asList("CVE-2023-9999"));

        nodes.add(node1);
        nodes.add(node2);
        return nodes;
    }

    private List<Map<String, Object>> getMockEdges() {
        List<Map<String, Object>> edges = new ArrayList<>();
        Map<String, Object> edge1 = new HashMap<>();
        edge1.put("source", "web_server");
        edge1.put("target", "database");
        edge1.put("label", "SQL Injection");
        edge1.put("risk", "HIGH");

        edges.add(edge1);
        return edges;
    }
}
