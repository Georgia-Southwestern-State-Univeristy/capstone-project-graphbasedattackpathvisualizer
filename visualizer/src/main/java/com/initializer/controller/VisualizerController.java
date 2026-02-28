package com.initializer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;

import com.initializer.graph.Node;
import com.initializer.services.GraphEdgeDTO;
import com.initializer.services.GraphService;
import com.initializer.services.MitigationDTO;
import com.initializer.services.ShortestPathService;
import com.initializer.services.AttackPathResult;
import com.initializer.entity.BusinessProfileEntity;
import com.initializer.services.BusinessProfileService;

// REST controller for exposing attack graph structure.

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class VisualizerController {

    @Autowired
    private GraphService graphService;

    @Autowired
    private ShortestPathService shortestPathService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BusinessProfileService businessProfileService;
    
    // Health check endpoint.
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Visualizer API is running");
    }
    @GetMapping("/test-db")
    public ResponseEntity<String> testDatabase() {
        String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
        return ResponseEntity.ok("Database Connected: " + result);
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
            @RequestParam String target,
            @RequestParam(required = false) List<Integer> mitigations) {

        AttackPathResult result = shortestPathService.computeAttackPath(source, target, mitigations);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/mitigations")
    public ResponseEntity<List<MitigationDTO>> getMitigations() {

        return ResponseEntity.ok(graphService.getMitigations());
    }


    // REST endpoint to create and persist a new BusinessProfile configuration.
    // Expects JSON body containing infrastructure toggles.
    // Returns 201 Created with the saved profile.
    @PostMapping("/profile")
    public ResponseEntity<BusinessProfileEntity> saveProfile(
            @RequestBody BusinessProfileEntity profile) {

        BusinessProfileEntity savedProfile =
                businessProfileService.saveProfile(profile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProfile);
    }


    // REST endpoint to retrieve the most recently created BusinessProfile.
    // Returns 200 with profile if found, or 404 if no profile exists.
    @GetMapping("/profile")
    public ResponseEntity<BusinessProfileEntity> getLatestProfile() {

        BusinessProfileEntity profile =
                businessProfileService.getLatestProfile();

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }

}
