package com.initializer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import com.initializer.graph.Node;
import com.initializer.services.GraphEdgeDTO;
import com.initializer.services.GraphService;
import com.initializer.services.MitigationDTO;
import com.initializer.services.ShortestPathService;
import com.initializer.services.AttackPathResult;
import com.initializer.entity.BusinessProfileEntity;
import com.initializer.entity.UserEntity;
import com.initializer.services.BusinessProfileService;
import com.initializer.services.UserService;
import com.initializer.services.BusinessProfileDTO;

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

    @Autowired
    private UserService userService;
    
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

    // Returns the attack graph structure filtered by the authenticated user's BusinessProfile.
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph(Authentication authentication) {

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        var graph = graphService.getFilteredGraph(profile, null);

        List<Node> nodes = new ArrayList<>(graph.vertexSet());
        List<GraphEdgeDTO> edges = new ArrayList<>();

        for (var edge : graph.edgeSet()) {

            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);

            edges.add(new GraphEdgeDTO(
                    source.getId(),
                    target.getId(),
                    edge.getAttackAction(),
                    edge.getWeight()
            ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("nodes", nodes);
        response.put("edges", edges);

        return ResponseEntity.ok(response);
    }

    // REST endpoint to compute shortest attack path
    @GetMapping("/path")
    public ResponseEntity<AttackPathResult> getAttackPath(
            @RequestParam String source,
            @RequestParam String target,
            @RequestParam(required = false) List<Integer> mitigations,
            Authentication authentication) {

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AttackPathResult result =
                shortestPathService.computeAttackPath(user, source, target, mitigations);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/mitigations")
    public ResponseEntity<List<MitigationDTO>> getMitigations(Authentication authentication) {

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                graphService.getMitigations(profile)
        );
    }

    // CREATE or UPDATE profile (DTO response)
    @PostMapping("/profile")
    public ResponseEntity<BusinessProfileDTO> saveProfile(
            @RequestBody BusinessProfileEntity profile,
            Authentication authentication) {

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BusinessProfileEntity existingProfile =
                businessProfileService.getProfileByUser(user);

        BusinessProfileEntity savedProfile;

        if (existingProfile != null) {
            existingProfile.setUsesVPN(profile.isUsesVPN());
            existingProfile.setHasFileServer(profile.isHasFileServer());
            existingProfile.setUsesSaaS(profile.isUsesSaaS());
            existingProfile.setHasPublicWebApp(profile.isHasPublicWebApp());
            existingProfile.setUsesIdentityProvider(profile.isUsesIdentityProvider());
            existingProfile.setHasEmailServer(profile.isHasEmailServer());
            existingProfile.setHasDomainController(profile.isHasDomainController());
            existingProfile.setHasInternalApp(profile.isHasInternalApp());
            existingProfile.setHasHRSystem(profile.isHasHRSystem());
            existingProfile.setHasFinanceSystem(profile.isHasFinanceSystem());
            existingProfile.setHasBackupServer(profile.isHasBackupServer());
            existingProfile.setHasMDMServer(profile.isHasMDMServer());
            existingProfile.setHasWirelessAccessPoint(profile.isHasWirelessAccessPoint());
            existingProfile.setHasFirewall(profile.isHasFirewall());
            existingProfile.setHasDNSServer(profile.isHasDNSServer());

            savedProfile = businessProfileService.saveProfile(existingProfile);

        } else {
            profile.setUser(user);
            savedProfile = businessProfileService.saveProfile(profile);
        }

        BusinessProfileDTO dto = new BusinessProfileDTO(
                savedProfile.getProfileID(),
                savedProfile.isUsesVPN(),
                savedProfile.isHasFileServer(),
                savedProfile.isUsesSaaS(),
                savedProfile.isHasPublicWebApp(),
                savedProfile.isUsesIdentityProvider(),
                savedProfile.isHasEmailServer(),
                savedProfile.isHasDomainController(),
                savedProfile.isHasInternalApp(),
                savedProfile.isHasHRSystem(),
                savedProfile.isHasFinanceSystem(),
                savedProfile.isHasBackupServer(),
                savedProfile.isHasMDMServer(),
                savedProfile.isHasWirelessAccessPoint(),
                savedProfile.isHasFirewall(),
                savedProfile.isHasDNSServer()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // GET profile (DTO response)
    @GetMapping("/profile")
    public ResponseEntity<BusinessProfileDTO> getProfile(Authentication authentication) {

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        BusinessProfileDTO dto = new BusinessProfileDTO(
                profile.getProfileID(),
                profile.isUsesVPN(),
                profile.isHasFileServer(),
                profile.isUsesSaaS(),
                profile.isHasPublicWebApp(),
                profile.isUsesIdentityProvider(),
                profile.isHasEmailServer(),
                profile.isHasDomainController(),
                profile.isHasInternalApp(),
                profile.isHasHRSystem(),
                profile.isHasFinanceSystem(),
                profile.isHasBackupServer(),
                profile.isHasMDMServer(),
                profile.isHasWirelessAccessPoint(),
                profile.isHasFirewall(),
                profile.isHasDNSServer()
        );

        return ResponseEntity.ok(dto);
    }
}