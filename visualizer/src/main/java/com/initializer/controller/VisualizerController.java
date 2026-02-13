package com.initializer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.initializer.graph.Node;
import com.initializer.services.GraphEdgeDTO;
import com.initializer.services.GraphService;
import com.initializer.services.ShortestPathService;
import com.initializer.services.AttackPathResult;


// REST controller for exposing attack graph structure.

@RestController
@RequestMapping("/api")
public class VisualizerController {

    @Autowired
    private GraphService graphService;

    @Autowired
    private ShortestPathService shortestPathService;

    
    // Health check endpoint.
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Visualizer API is running");
    }

    
    // Returns the full attack graph structure (nodes + edges).
    
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph() {

        List<Node> nodes = graphService.getNodes();
        List<GraphEdgeDTO> edges = graphService.getEdges();

        Map<String, Object> response = new HashMap<>();
        response.put("nodes", nodes);
        response.put("edges", edges);

        return ResponseEntity.ok(response);
    }


    // REST endpoint to compute and return the shortest attack path between two nodes 
    // Expects 'source' and 'target' parameters.

    @GetMapping("/path")
    public ResponseEntity<AttackPathResult> getAttackPath(
            @RequestParam String source,
            @RequestParam String target) {

        AttackPathResult result = shortestPathService.computeAttackPath(source, target);

        return ResponseEntity.ok(result);
    }
}
