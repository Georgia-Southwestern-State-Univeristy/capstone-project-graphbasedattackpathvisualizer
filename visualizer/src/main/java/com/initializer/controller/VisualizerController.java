package com.initializer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.initializer.graph.Edge;
import com.initializer.graph.Node;
import com.initializer.service.GraphService;

@RestController
@RequestMapping("/api")
public class VisualizerController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/health")
    public String health() {
        return "Visualizer API is running";
    }

    // Graph retrieval endpoints
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph() {
        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", getNodes().getBody());
        graph.put("edges", getEdges().getBody());
        return ResponseEntity.ok(graph);
    }

    @GetMapping("/graph/nodes")
    public ResponseEntity<List<Map<String, Object>>> getNodes() {
        List<Node> nodes = graphService.getNodes();
        List<Map<String, Object>> nodeMaps = new ArrayList<>();
        for (Node node : nodes) {
            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("id", node.getId());
            nodeMap.put("type", node.getType().toString());
            nodeMaps.add(nodeMap);
        }
        return ResponseEntity.ok(nodeMaps);
    }

    @GetMapping("/graph/edges")
    public ResponseEntity<List<Map<String, Object>>> getEdges() {
        List<Edge> edges = graphService.getEdges();
        List<Map<String, Object>> edgeMaps = new ArrayList<>();
        for (Edge edge : edges) {
            Map<String, Object> edgeMap = new HashMap<>();
            edgeMap.put("source", edge.getSourceId());
            edgeMap.put("target", edge.getTargetId());
            edgeMap.put("attackAction", edge.getAttackAction());
            edgeMap.put("weight", edge.getWeight());
            edgeMaps.add(edgeMap);
        }
        return ResponseEntity.ok(edgeMaps);
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

}
